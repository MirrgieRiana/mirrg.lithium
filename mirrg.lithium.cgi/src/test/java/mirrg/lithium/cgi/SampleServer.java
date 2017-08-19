package mirrg.lithium.cgi;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

import com.sun.net.httpserver.HttpServer;

import mirrg.lithium.struct.Struct1;

public class SampleServer
{

	public static void main(String[] args) throws IOException
	{
		Struct1<CGIServerSetting> sCgiServerSetting = new Struct1<>();
		HttpServer httpServer = HttpServer.create();
		httpServer.createContext("/", e -> new Thread(() -> {
			File scriptFile = new File("cgi-bin" + e.getRequestURI().getPath());
			if (scriptFile.isFile()) {
				new CGIRunner(
					sCgiServerSetting.x,
					e,
					new String[] { "perl", "%s" },
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
					},
					1000).run();
			} else {
				try {
					HTTPResponse.get(404).sendResponse(e);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}).start());
		httpServer.bind(new InetSocketAddress("127.0.0.1", 0), 10);
		sCgiServerSetting.x = new CGIServerSetting(
			httpServer.getAddress().getPort(),
			TestCgi.class.getName(),
			new File("cgi-bin"),
			1000,
			new CGIBufferPool(100 * 1000, 10 * 1000 * 1000, 10, 10000));
		httpServer.start();

		System.out.println("http://localhost:" + httpServer.getAddress().getPort());
	}

}
