package mirrg.applications.service.pwi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.channels.ClosedByInterruptException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import mirrg.applications.service.pwi.BlockWeb.WebSettings.CGISetting;
import mirrg.lithium.struct.Tuple;

public class BlockWeb extends BlockBase
{

	private WebSettings settings;
	private LineSource source;
	private ILineReceiver receiver;

	public BlockWeb(Logger logger, WebSettings settings, LineSource source, ILineReceiver receiver) throws Exception
	{
		super(logger);
		this.settings = settings;
		this.source = source;
		this.receiver = receiver;
	}

	public static final DateTimeFormatter FORMATTER_LOG = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

	@Override
	protected Thread createThread() throws Exception
	{

		// create server
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(settings.host, settings.port), settings.backlog);
		{
			HttpContext context = httpServer.createContext("/", e -> {
				String path = e.getRequestURI().getPath();

				if (path.toString().matches("/api(|/.*)")) {
					String username = e.getPrincipal() == null ? "Guest" : e.getPrincipal().getUsername();

					if (path.toString().matches("/api/log")) {
						send(e, String.format(
							"<link rel='stylesheet' href='/log.css'><div class='username'>" + username + "</div><table>%s</table>",
							settings.lineStorage.stream()
								.map(t -> String.format(
									"<tr style=\"color: %s;\"><td class='id'>%s</td><td class='time'>[%s]</td><td class='source'><b>%s</b></td><td class='text'>%s</td></tr>",
									t.y.source.color,
									t.x,
									t.y.time.format(FORMATTER_LOG),
									t.y.source.name,
									t.y.text))
								.collect(Collectors.joining())));
						return;
					}

					if (path.toString().matches("/api/log/count")) {
						send(e, "" + settings.lineStorage.getCount());
						return;
					}

					if (path.toString().matches("/api/send")) {
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

				if (path.toString().matches("/")) {
					redirect(e, "/index.html");
					return;
				}

				if (!path.contains("/..")) {

					for (String dir : settings.homeDirectory) {
						File file = new File(dir, path);
						if (file.exists()) {

							for (CGISetting cgiSetting : settings.cgiSettings) {
								if (file.getPath().endsWith(cgiSetting.fileNameSuffix)) {
									doCGI(e, cgiSetting, file);
									return;
								}
							}

							sendFile(e, file.toURI().toURL());
							return;
						}
					}

					send(e, 404, "404");
					return;
				}

				send(e, 404, "404");
				return;

			});
			if (settings.needAuthentication) {
				context.setAuthenticator(new BasicAuthenticator("Controller") {
					@Override
					public boolean checkCredentials(String arg0, String arg1)
					{
						if (!settings.basicAuthenticationRegex.isEmpty()) {
							if (!arg0.contains("\n")) {
								if ((arg0 + "\n" + arg1).matches(settings.basicAuthenticationRegex)) {
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
			logger.log("Web server started on http://" + settings.host + ":" + settings.port);

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

	private void doCGI(HttpExchange httpExchange, CGISetting cgiSetting, File scriptFile)
	{
		String[] command = Stream.of(cgiSetting.command)
			.map(s -> s.replace("%s", scriptFile.getPath()))
			.toArray(String[]::new);

		try {

			ArrayList<Tuple<byte[], Integer>> buffersIn = new ArrayList<>();
			try (InputStream in = httpExchange.getRequestBody()) {
				while (true) {
					byte[] buffer = new byte[4000];
					int len = in.read(buffer);
					if (len == -1) break;
					buffersIn.add(new Tuple<>(buffer, len));
				}
			} catch (IOException e) {
				logger.log(e);
			}

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.directory(scriptFile.getAbsoluteFile().getParentFile());

			for (Entry<String, List<String>> entry : httpExchange.getRequestHeaders().entrySet()) {
				for (String value : entry.getValue()) {
					putEnvironment(processBuilder, "HTTP_" + entry.getKey().toUpperCase().replaceAll("-", "_"), value);
				}
			}

			{
				putEnvironment(processBuilder, "CONTENT_LENGTH", "" + buffersIn.stream()
					.mapToInt(t -> t.y)
					.sum());
				putEnvironment(processBuilder, "CONTENT_TYPE", httpExchange.getRequestHeaders().getFirst("content-type"));
				putEnvironment(processBuilder, "GATEWAY_INTERFACE", "CGI/1.1");
				putEnvironment(processBuilder, "PATH_INFO", ""); // TODO path info
				putEnvironment(processBuilder, "PATH_TRANSLATED", ""); // TODO path info
				putEnvironment(processBuilder, "QUERY_STRING", httpExchange.getRequestURI().getRawQuery());
				putEnvironment(processBuilder, "REMOTE_ADDR", httpExchange.getRemoteAddress().getAddress().getHostAddress());
				putEnvironment(processBuilder, "REMOTE_HOST", httpExchange.getRemoteAddress().getHostName());
				putEnvironment(processBuilder, "REMOTE_PORT", "" + httpExchange.getRemoteAddress().getPort());
				putEnvironment(processBuilder, "REQUEST_METHOD", httpExchange.getRequestMethod());
				putEnvironment(processBuilder, "REQUEST_URI", httpExchange.getRequestURI().getPath());
				putEnvironment(processBuilder, "DOCUMENT_ROOT", settings.homeDirectory[0]);
				putEnvironment(processBuilder, "SCRIPT_FILENAME", scriptFile.getAbsolutePath());
				putEnvironment(processBuilder, "SCRIPT_NAME", httpExchange.getRequestURI().getPath());
				putEnvironment(processBuilder, "SERVER_NAME", "" + settings.name);
				putEnvironment(processBuilder, "SERVER_PORT", "" + settings.port);
				putEnvironment(processBuilder, "SERVER_PROTOCOL", "HTTP/1.1");
				putEnvironment(processBuilder, "SERVER_SOFTWARE", getServerName() + "/" + getServerVersion());
			}

			Process process = processBuilder.start();

			Thread threadIn = new Thread(() -> {
				try (OutputStream out = process.getOutputStream()) {
					for (Tuple<byte[], Integer> buffer : buffersIn) {
						out.write(buffer.x, 0, buffer.y);
					}
				} catch (ClosedByInterruptException e) {

				} catch (IOException e) {
					logger.log(e);
				}
			});
			threadIn.start();

			Thread threadOut = new Thread(() -> {
				try (InputStream in = process.getInputStream();
					OutputStream out = httpExchange.getResponseBody()) {

					int code = 200;

					// ヘッダ抽出
					ArrayList<Byte> sb = new ArrayList<>();
					while (true) {
						int ch = in.read();

						if (ch == -1) {
							httpExchange.sendResponseHeaders(200, 0);
							return;
						}

						if (ch == '\n' || ch == '\r') {
							// 改行が来た

							// 次が\rなら読み飛ばし
							if (ch == '\r') {
								in.mark(1);
								if (in.read() != '\n') in.reset();
							}

							byte[] bytes = new byte[sb.size()];
							for (int i = 0; i < sb.size(); i++) {
								bytes[i] = sb.get(i);
							}
							String line = new String(bytes);

							if (line.equals("")) {
								// ヘッダ終了
								break;
							}

							Matcher m = Pattern.compile("([^:]*)[ \t]*:[ \t]*(.*)").matcher(line);
							if (m.matches()) {
								// ヘッダ行

								httpExchange.getResponseHeaders().add(
									m.group(1),
									m.group(2));
								if (m.group(1).toLowerCase().equals("status")) {
									// "302 Moved" とか
									String[] strs = m.group(2).split(" ");
									if (strs.length >= 1) {
										try {
											code = Integer.parseInt(strs[0], 10);
										} catch (NumberFormatException e) {

										}
									}
								}
							} else {
								// "HTTP/1.0 302 Moved" とか
								String[] strs = line.split(" ");
								if (strs.length >= 2) {
									try {
										code = Integer.parseInt(strs[1], 10);
									} catch (NumberFormatException e) {

									}
								}
							}

							sb = new ArrayList<>();
						} else {
							// 改行以外が来た

							sb.add((byte) ch);
						}

					}

					ArrayList<Tuple<byte[], Integer>> buffersOut = new ArrayList<>();
					while (true) {
						byte[] buffer = new byte[4000];
						int len = in.read(buffer);
						if (len == -1) break;
						buffersOut.add(new Tuple<>(buffer, len));
					}

					httpExchange.sendResponseHeaders(code, buffersOut.stream()
						.mapToInt(t -> t.y)
						.sum());
					for (Tuple<byte[], Integer> buffer : buffersOut) {
						out.write(buffer.x, 0, buffer.y);
					}

				} catch (ClosedByInterruptException e) {

				} catch (IOException e) {
					logger.log(e);
				}
			});
			threadOut.start();

			Thread threadErr = new Thread(() -> {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
					while (true) {
						try {
							String line = in.readLine();
							if (line == null) break;
							logger.log(line);
						} catch (IOException e) {
							logger.log(e);
							break;
						}
					}
				} catch (ClosedByInterruptException e) {

				} catch (IOException e) {
					logger.log(e);
				}
			});
			threadErr.start();

			process.waitFor(1, TimeUnit.SECONDS);
			if (process.isAlive()) process.destroyForcibly();

			threadIn.interrupt();
			threadOut.interrupt();
			threadErr.interrupt();

		} catch (Exception e) {
			logger.log(e);
		}

	}

	private void putEnvironment(ProcessBuilder processBuilder, String key, String value)
	{
		if (value == null) value = "";
		processBuilder.environment().put(key, value);
	}

	public String getServerName()
	{
		return Main.class.getPackage().getName();
	}

	public String getServerVersion()
	{
		return "undefined"; // TODO
	}

	public static class WebSettings
	{

		public String host;
		public String name;
		public int port;
		public int backlog;

		public String[] homeDirectory;
		public CGISetting[] cgiSettings;

		public boolean needAuthentication;
		public String basicAuthenticationRegex;

		public LineStorage lineStorage;

		public static class CGISetting
		{

			public String fileNameSuffix;
			public String[] command;

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
				cgiSettings[i].command = strings[i].substring(strings[i].indexOf(":") + 1).split(" ");
			}
			return cgiSettings;
		}

	}

}
