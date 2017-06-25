package mirrg.applications.service.pwi.dispatchers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import mirrg.applications.service.pw2.LineStorage;
import mirrg.applications.service.pw2.Logger;
import mirrg.applications.service.pwi.core.ILineReceiver;
import mirrg.applications.service.pwi.core.Line;
import mirrg.applications.service.pwi.core.LineSource;
import mirrg.applications.service.pwi.n.Config;
import mirrg.applications.service.pwi.n.Config.ConfigWeb;
import mirrg.lithium.struct.Tuple;

public class LineDispatcherWebInterface extends LineDispatcherBase
{

	private Logger logger;
	private ILineReceiver receiver;
	private LineSource source;
	private ConfigWeb config;
	private LineStorage lineStorage;

	public LineDispatcherWebInterface(Logger logger, ILineReceiver receiver, LineSource source, ConfigWeb config, LineStorage lineStorage)
	{
		this.logger = logger;
		this.receiver = receiver;
		this.source = source;
		this.config = config;
		this.lineStorage = lineStorage;
	}

	public static final DateTimeFormatter FORMATTER_LOG = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

	@Override
	protected Thread createThread() throws Exception
	{

		// create server
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(config.hostname, config.port), config.backlog);
		{
			HttpContext context = httpServer.createContext("/", e -> {

				String path = e.getRequestURI().getPath();
				if (path.toString().matches("/api(|/.*)")) {

					if (path.toString().matches("/api/log")) {
						send(e, String.format(
							"<link rel='stylesheet' href='/log.css'><div class='username'>" + e.getPrincipal().getUsername() + "</div><table>%s</table>",
							lineStorage.stream()
								.map(t -> String.format(
									"<tr style=\"color: %s;\"><td class='id'>%s</td><td class='time'>[%s]</td><td class='source'><b>%s</b></td><td class='text'>%s</td></tr>",
									t.y.source.color,
									t.x,
									t.y.time.format(FORMATTER_LOG),
									t.y.source.name,
									t.y.text))
								.collect(Collectors.joining())));
					} else if (path.toString().matches("/api/log/count")) {
						send(e, "" + lineStorage.getCount());
					} else if (path.toString().matches("/api/send")) {
						String query = e.getRequestURI().getQuery();
						if (query == null) {
							send(e, 400, "400");
						} else {

							logger.log("Access: " + e.getRequestURI() + " " + e.getRemoteAddress());
							receiver.onLine(logger, new Line(new LineSource(source.name + "(" + e.getPrincipal().getUsername() + ")", source.color), query));

							send(e, "Success[" + query + "]");
						}
					} else {
						send(e, 404, "404");
					}

				} else if (path.toString().matches("/")) {
					redirect(e, "/index.html");
				} else if (!path.contains("/..")) {
					sendFile(e, new File(config.homeDirectory, path).toURI().toURL());
				} else {
					send(e, 404, "404");
				}

			});
			if (config.needAuthentication) {
				context.setAuthenticator(new BasicAuthenticator("Controller") {
					@Override
					public boolean checkCredentials(String arg0, String arg1)
					{
						if (!arg0.matches(config.user)) return false;
						if (!config.password.equals(arg1)) return false;
						return true;
					}
				});
			}
		}

		return new Thread(() -> {

			httpServer.start();
			logger.log("Web server started on http://" + config.hostname + ":" + config.port);

			while (true) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					break;
				}
			}

			receiver.onClosed(logger, source);

		}, source.name);
	}

	private static void redirect(HttpExchange e, String string) throws IOException
	{
		e.getResponseHeaders().add("Location", string);
		e.sendResponseHeaders(301, 0);
		e.getResponseBody().close();
	}

	private static void send(HttpExchange e, String text) throws IOException
	{
		send(e, 200, "text/html", text, "utf-8");
	}

	private static void send(HttpExchange e, int code, String text) throws IOException
	{
		send(e, code, "text/html", text, "utf-8");
	}

	private static void send(HttpExchange e, int code, String contentType, String text, String charset) throws IOException
	{
		e.getResponseHeaders().add("Content-Type", contentType + "; charset= " + charset);
		byte[] bytes = text.getBytes(charset);
		e.sendResponseHeaders(code, bytes.length);
		e.getResponseBody().write(bytes);
		e.getResponseBody().close();
	}

	private static void sendFile(HttpExchange e, URL url) throws IOException
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

			e.sendResponseHeaders(200, buffers.stream()
				.mapToInt(t -> t.y)
				.sum());
			for (Tuple<byte[], Integer> buffer : buffers) {
				e.getResponseBody().write(buffer.x, 0, buffer.y);
			}
			e.getResponseBody().close();
		} catch (IOException e2) {
			send(e, 404, "404");
		}
	}

}
