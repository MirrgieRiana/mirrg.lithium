package mirrg.lithium.cgi;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.IntConsumer;

import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

import mirrg.lithium.struct.Struct1;

public class TestCgi
{

	@Test
	public void test() throws Exception
	{
		startServer(port -> {
			Struct1<Boolean> flag = new Struct1<>(false);
			ArrayList<Thread> threads = new ArrayList<>();

			for (int i = 0; i < 10; i++) {
				threads.add(new Thread(() -> {
					try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://localhost:" + port + "/cgitest1.pl").openStream()))) {
						assertEquals("<h1>Test</h1>", in.readLine());
						assertEquals(null, in.readLine());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}));
			}
			threads.add(new Thread(() -> {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://localhost:" + port + "/cgitest2.pl?id=a%20b").openStream()))) {
					assertEquals("a b", in.readLine());
					assertEquals(null, in.readLine());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}));

			for (Thread thread : threads) {
				thread.setUncaughtExceptionHandler((t, e) -> {
					flag.x = true;
				});
			}

			for (Thread thread : threads) {
				thread.start();
			}

			try {
				for (Thread thread : threads) {
					thread.join();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (flag.x) {
				fail();
			}

		});
	}

	public void startServer(IntConsumer yield) throws IOException
	{
		Struct1<CGIServerSetting> sCgiServerSetting = new Struct1<>();
		HttpServer httpServer = HttpServer.create();
		httpServer.createContext("/", e -> new Thread(() -> new CGIRunner(
			sCgiServerSetting.x,
			e,
			new String[] { "perl", "%s" },
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
			},
			1000).run()).start());
		httpServer.bind(new InetSocketAddress("127.0.0.1", 0), 10);
		sCgiServerSetting.x = new CGIServerSetting(
			httpServer.getAddress().getPort(),
			TestCgi.class.getName(),
			new File("cgi-bin"),
			1000,
			new CGIBufferPool(50, 50, 10, 10000));
		httpServer.start();

		yield.accept(httpServer.getAddress().getPort());

		httpServer.stop(1);
	}

}
