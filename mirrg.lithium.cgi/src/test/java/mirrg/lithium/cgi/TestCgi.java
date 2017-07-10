package mirrg.lithium.cgi;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Optional;
import java.util.function.IntConsumer;

import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

import mirrg.lithium.struct.ImmutableArray;

public class TestCgi
{

	@Test
	public void test() throws Exception
	{
		startServer(port -> {

			try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://localhost:" + port + "/cgitest1.pl").openStream()))) {
				assertEquals("<h1>Test</h1>", in.readLine());
				assertEquals(null, in.readLine());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://localhost:" + port + "/cgitest2.pl?id=a%20b").openStream()))) {
				assertEquals("a b", in.readLine());
				assertEquals(null, in.readLine());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		});
	}

	public void startServer(IntConsumer yield) throws IOException
	{
		CGISettings[] cgiEnvironment = {
			null
		};

		HttpServer httpServer = HttpServer.create();
		httpServer.createContext("/", e -> cgiEnvironment[0].doCGI(
			e,
			new File("cgi-bin" + e.getRequestURI().getPath()),
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
			}));
		httpServer.bind(new InetSocketAddress("127.0.0.1", 0), 10);
		cgiEnvironment[0] = new CGISettings(
			"localhost",
			httpServer.getAddress().getPort(),
			TestCgi.class.getName(),
			new File("cgi-bin"),
			new ImmutableArray<>("perl", "%s"),
			1000,
			50,
			50);
		httpServer.start();

		yield.accept(httpServer.getAddress().getPort());

		httpServer.stop(1);
	}

}
