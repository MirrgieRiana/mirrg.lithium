package mirrg.lithium.cgi;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

import com.sun.net.httpserver.HttpServer;

import mirrg.lithium.struct.ImmutableArray;

public class SampleServer
{
	private static CGISettings cgiEnvironments;

	public static void main(String[] args) throws IOException
	{
		HttpServer httpServer = HttpServer.create();
		httpServer.createContext("/", e -> {
			File scriptFile = new File("cgi-bin" + e.getRequestURI().getPath());
			if (scriptFile.isFile()) {
				cgiEnvironments.doCGI(
					e,
					scriptFile,
					Optional.empty(),
					Optional.empty(),
					new ILogger() {
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
					});
			} else {
				HTTPResponse.get(404).sendResponse(e);
			}
		});
		httpServer.bind(new InetSocketAddress("127.0.0.1", 0), 10);
		cgiEnvironments = new CGISettings(
			httpServer.getAddress().getPort(),
			TestCgi.class.getName(),
			new File("cgi-bin"),
			new ImmutableArray<>("perl", "%s"),
			1000,
			1000,
			1000000);
		httpServer.start();

		System.out.println("http://localhost:" + httpServer.getAddress().getPort());
	}

}
