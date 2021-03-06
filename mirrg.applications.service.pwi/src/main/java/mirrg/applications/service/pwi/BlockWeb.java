package mirrg.applications.service.pwi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import mirrg.applications.service.pwi.BlockWeb.WebSetting.CGISetting;
import mirrg.lithium.cgi.CGIBufferPool;
import mirrg.lithium.cgi.CGIRunner;
import mirrg.lithium.cgi.CGIServerSetting;
import mirrg.lithium.cgi.ILogger;
import mirrg.lithium.struct.Tuple;

public class BlockWeb extends BlockBase
{

	private WebSetting setting;
	private LineSource source;
	private ILineReceiver receiver;

	public BlockWeb(Logger logger, WebSetting setting, LineSource source, ILineReceiver receiver) throws Exception
	{
		super(logger);
		this.setting = setting;
		this.source = source;
		this.receiver = receiver;
	}

	public static final DateTimeFormatter FORMATTER_LOG = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

	@Override
	protected Thread createThread() throws Exception
	{
		CGIBufferPool cgiBufferPool = new CGIBufferPool(
			setting.requestBufferSize,
			setting.responseBufferSize,
			10,
			10000);

		// create server
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(setting.host, setting.port), setting.backlog);
		{
			HttpContext context = httpServer.createContext("/", e -> {
				new Thread(() -> {
					try {
						String path = e.getRequestURI().getPath();
						String username = e.getPrincipal() == null ? "Guest" : e.getPrincipal().getUsername();

						// 異常URLの除去
						if ((path + "/").indexOf("/../") != -1) {
							send(e, 403, "403");
							return;
						}

						// API
						if ((path + "/").startsWith("/api/")) {

							if (path.equals("/api/log")) {
								send(e, String.format(
									"<link rel='stylesheet' href='/log.css'><div class='username'>" + username + "</div><table>%s</table>",
									setting.lineStorage.stream()
										.map(t -> String.format(
											"<tr style=\"color: %s;\"><td class='id'>%s</td><td class='time'>[%s]</td><td class='source'><b>%s</b></td><td class='text'>%s</td></tr>",
											t.y.source.color,
											t.x,
											t.y.time.format(FORMATTER_LOG),
											t.y.source.name,
											t.y.text
												.replaceAll("&", "&amp;")
												.replaceAll("<", "&lt;")
												.replaceAll(">", "&gt;")
												.replaceAll(" ", "&nbsp;")
												.replaceAll("\n", "<br>")))
										.collect(Collectors.joining())));
								return;
							}

							if (path.equals("/api/log/count")) {
								send(e, "" + setting.lineStorage.getCount());
								return;
							}

							if (path.equals("/api/send")) {
								String query = e.getRequestURI().getQuery();
								if (query == null) {
									send(e, 400, "400");
								} else {

									logger.log("Access: " + e.getRequestURI() + " " + e.getRemoteAddress());
									try {
										receiver.onLine(new Line(new LineSource(source.name + "(" + username + ")", source.color), query));
									} catch (Exception e2) {
										logger.log(e2);
									}

									send(e, "Success[" + query + "]");
								}
								return;
							}

							send(e, 404, "404");
							return;
						}

						// index
						if (path.endsWith("/")) {

							for (String dir : setting.homeDirectory) {
								for (String index : setting.indexes) {
									File file = new File(dir, path.substring(1) + index);
									if (file.isFile()) {
										redirect(e, path + index);
										return;
									}
								}
							}

							send(e, 404, "404");
							return;
						}

						// CGI/ファイル転送
						for (String dir : setting.homeDirectory) {
							File file = new File(dir, path.substring(1));
							if (file.isFile()) {

								for (CGISetting cgiSetting : setting.cgiSettings) {
									if (file.getPath().endsWith(cgiSetting.fileNameSuffix)) {
										new CGIRunner(
											new CGIServerSetting(
												setting.port,
												getServerName() + "/" + getServerVersion(),
												new File(dir),
												setting.timeoutMs,
												cgiBufferPool),
											e,
											cgiSetting.commandFormat,
											file,
											Optional.empty(), // TODO
											Optional.empty(), // TODO
											new ILogger() {
												@Override
												public void accept(Exception e)
												{
													logger.log(e);
												}

												@Override
												public void accept(String message)
												{
													logger.log(message);
												}
											},
											1000).run();
										return;
									}
								}

								sendFile(e, file.toURI().toURL());
								return;
							}
						}

						send(e, 404, "404");
						return;
					} catch (IOException e1) {
						try {
							send(e, 500, "500");
						} catch (IOException e2) {
							e2.addSuppressed(e1);
							e2.printStackTrace();
						}
						return;
					}
				}).start();
			});
			if (setting.needAuthentication) {
				context.setAuthenticator(new BasicAuthenticator("Controller") {
					@Override
					public boolean checkCredentials(String arg0, String arg1)
					{
						if (!setting.basicAuthenticationRegex.isEmpty()) {
							if (!arg0.contains("\n")) {
								if ((arg0 + "\n" + arg1).matches(setting.basicAuthenticationRegex)) {
									return true;
								}
							}
						}
						return false;
					}
				});
			}
		}

		return new Thread(() -> {

			httpServer.start();
			logger.log("Web server started on http://" + setting.host + ":" + setting.port);

			while (true) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					break;
				}
			}

		}, source.name);
	}

	private static void redirect(HttpExchange httpExchange, String string) throws IOException
	{
		httpExchange.getResponseHeaders().add("Location", string);
		httpExchange.sendResponseHeaders(301, 0);
		httpExchange.getResponseBody().close();
	}

	private static void send(HttpExchange httpExchange, String text) throws IOException
	{
		send(httpExchange, 200, "text/html", text, "utf-8");
	}

	private static void send(HttpExchange httpExchange, int code, String text) throws IOException
	{
		send(httpExchange, code, "text/html", text, "utf-8");
	}

	private static void send(HttpExchange httpExchange, int code, String contentType, String text, String charset) throws IOException
	{
		httpExchange.getResponseHeaders().add("Content-Type", contentType + "; charset= " + charset);
		byte[] bytes = text.getBytes(charset);
		httpExchange.sendResponseHeaders(code, bytes.length);
		httpExchange.getResponseBody().write(bytes);
		httpExchange.getResponseBody().close();
	}

	private static void sendFile(HttpExchange httpExchange, URL url) throws IOException
	{
		try {
			InputStream in = url.openStream();

			ArrayList<Tuple<byte[], Integer>> buffers = new ArrayList<>();
			while (true) {
				byte[] buffer = new byte[4000];
				int len = in.read(buffer);
				if (len == -1) break;
				buffers.add(new Tuple<>(buffer, len));
			}
			in.close();

			httpExchange.sendResponseHeaders(200, buffers.stream()
				.mapToInt(t -> t.y)
				.sum());
			for (Tuple<byte[], Integer> buffer : buffers) {
				httpExchange.getResponseBody().write(buffer.x, 0, buffer.y);
			}
			httpExchange.getResponseBody().close();
		} catch (IOException e2) {
			send(httpExchange, 404, "404");
		}
	}

	public String getServerName()
	{
		return Main.class.getPackage().getName();
	}

	public String getServerVersion()
	{
		return "undefined"; // TODO
	}

	public static class WebSetting
	{

		public String host;
		public int port;
		public int backlog;

		public String[] homeDirectory;
		public CGISetting[] cgiSettings;
		public String[] indexes;

		public int timeoutMs;
		public int requestBufferSize;
		public int responseBufferSize;

		public boolean needAuthentication;
		public String basicAuthenticationRegex;

		public LineStorage lineStorage;

		public static class CGISetting
		{

			public String fileNameSuffix;
			public String[] commandFormat;

		}

		protected String[] parseHomeDirectory(String string)
		{
			// TODO use parser
			return string.split(";");
		}

		protected CGISetting[] parseCgiSettings(String string)
		{
			// TODO  use parser
			if (string.equals("")) return new CGISetting[0];
			String[] strings = string.split(";");
			CGISetting[] cgiSettings = new CGISetting[strings.length];
			for (int i = 0; i < strings.length; i++) {
				cgiSettings[i] = new CGISetting();
				cgiSettings[i].fileNameSuffix = strings[i].substring(0, strings[i].indexOf(":"));
				cgiSettings[i].commandFormat = strings[i].substring(strings[i].indexOf(":") + 1).split(" ");
			}
			return cgiSettings;
		}

		protected String[] parseIndexes(String string)
		{
			// TODO  use parser
			if (string.equals("")) return new String[0];
			return string.split(";");
		}

	}

}
