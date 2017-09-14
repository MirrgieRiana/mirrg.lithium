package mirrg.lithium.cgi;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import mirrg.lithium.cgi.routing.CGIPattern;
import mirrg.lithium.cgi.routing.CGIRouter;
import mirrg.lithium.cgi.routing.CGIRouter.EnumRouteResult;
import mirrg.lithium.struct.Struct1;
import mirrg.lithium.struct.Tuple;

public class SampleRouter
{

	public static void main(String[] args) throws IOException
	{
		CGIBufferPool cgiBufferPool = new CGIBufferPool(
			100 * 1000,
			10 * 1000 * 1000,
			10,
			10000);

		Struct1<CGIRouter> sCgiRouter = new Struct1<>();

		HttpServer httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 10);
		httpServer.createContext("/", e -> {
			new Thread(() -> {
				try {
					Tuple<EnumRouteResult, HTTPResponse> result = sCgiRouter.x.route(e.getRequestURI().getPath());
					if (result.x.found) {
						throw result.y;
					} else {
						throw HTTPResponse.get(404);
					}
				} catch (HTTPResponse httpResponse) {
					try {
						httpResponse.sendResponse(e);
						return;
					} catch (IOException e1) {
						try {
							HTTPResponse.get(500).sendResponse(e);
							return;
						} catch (IOException e2) {
							e2.addSuppressed(e1);
							e2.printStackTrace();
						}
					}
				}
			}).start();
		});
		httpServer.start();
		System.err.println("Server Start: http://localhost:" + httpServer.getAddress().getPort());

		ILogger logger = new ILogger() {
			@Override
			public void accept(String message)
			{
				System.err.println(message);
			}

			@Override
			public void accept(Exception e)
			{
				e.printStackTrace();
			}
		};

		sCgiRouter.x = new CGIRouter(new CGIServerSetting(
			httpServer.getAddress().getPort(),
			SampleRouter.class.getName(),
			new File("cgi-bin"),
			1000,
			cgiBufferPool));
		sCgiRouter.x.addIndex("index.html");
		sCgiRouter.x.addIndex("index.pl");
		sCgiRouter.x.addCGIPattern(new CGIPattern(
			".pl",
			new String[] { "perl", "%s" },
			logger,
			1000));
	}

}
