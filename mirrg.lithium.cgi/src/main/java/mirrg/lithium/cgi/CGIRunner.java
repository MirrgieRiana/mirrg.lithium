package mirrg.lithium.cgi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.ClosedByInterruptException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;

import mirrg.lithium.struct.Struct1;
import mirrg.lithium.struct.Struct2;
import mirrg.lithium.struct.Tuple;

public class CGIRunner
{

	private CGISettings cgiSettings;
	private HttpExchange httpExchange;
	private File scriptFile;
	private Optional<String> oPathInfo;
	private Optional<String> oPathTranslated;
	private ILogger logger;
	private Counter counter;

	public CGIRunner(
		CGISettings cgiEnvironment,
		HttpExchange httpExchange,
		File scriptFile,
		Optional<String> oPathInfo,
		Optional<String> oPathTranslated,
		ILogger logger,
		int counterLimit)
	{
		this.cgiSettings = cgiEnvironment;
		this.httpExchange = httpExchange;
		this.scriptFile = scriptFile;
		this.oPathInfo = oPathInfo;
		this.oPathTranslated = oPathTranslated;
		this.logger = logger;
		this.counter = new Counter(counterLimit);
	}

	public void doCGI()
	{
		try {
			try {
				throw doCGIImpl();
			} catch (RuntimeException e) {
				logger.accept(e);
				throw HTTPResponse.get(500);
			}
		} catch (HTTPResponse httpResponse) {
			if (httpResponse.isError) httpResponse.printStackTrace();
			try {
				httpResponse.sendResponse(httpExchange);
			} catch (IOException e) {
				logger.accept(e);
			}
		}
	}

	private HTTPResponse doCGIImpl() throws HTTPResponse
	{

		// リクエストを全て回収
		Struct2<byte[], Integer> requestBuffer = readRequest();

		// プロセスビルダの生成
		ProcessBuilder processBuilder = createProcessBuilder(requestBuffer.y);

		// プロセスの実行
		Struct2<byte[], Integer> responseBuffer = doProcess(processBuilder, requestBuffer);

		// レスポンスヘッダ抜き出し
		Tuple<String, Integer> res = splitResponse(responseBuffer);

		// ヘッダ処理
		int code = parseHeader(res.x);

		// クライアントに出力
		return HTTPResponse.get(code, responseBuffer.x, res.y, responseBuffer.y - res.y);

	}

	private Struct2<byte[], Integer> readRequest() throws HTTPResponse
	{
		try (InputStream in = httpExchange.getRequestBody()) {
			return cgiSettings.readRequest(in, logger);
		} catch (IOException e) {
			logger.accept(e);
			throw HTTPResponse.get(400);
		}
	}

	private ProcessBuilder createProcessBuilder(int contentLength)
	{
		ProcessBuilder processBuilder = new ProcessBuilder(cgiSettings.createCommand(scriptFile));
		processBuilder.directory(scriptFile.getAbsoluteFile().getParentFile());

		// HTTPで始まる環境変数の設定
		for (Entry<String, List<String>> entry : httpExchange.getRequestHeaders().entrySet()) {
			for (String value : entry.getValue()) {
				putEnvironment(processBuilder, "HTTP_" + entry.getKey().toUpperCase().replaceAll("-", "_"), value);
			}
			counter.count();
		}

		// その他の環境変数の設定
		{
			putEnvironment(processBuilder, "CONTENT_LENGTH", "" + contentLength);
			putEnvironment(processBuilder, "CONTENT_TYPE", httpExchange.getRequestHeaders().getFirst("content-type"));
			putEnvironment(processBuilder, "GATEWAY_INTERFACE", "CGI/1.1");
			putEnvironment(processBuilder, "PATH_INFO", oPathInfo.orElse(null));
			putEnvironment(processBuilder, "PATH_TRANSLATED", oPathTranslated.orElse(null));
			putEnvironment(processBuilder, "QUERY_STRING", httpExchange.getRequestURI().getRawQuery());
			putEnvironment(processBuilder, "REMOTE_ADDR", httpExchange.getRemoteAddress().getAddress().getHostAddress());
			putEnvironment(processBuilder, "REMOTE_HOST", httpExchange.getRemoteAddress().getHostName());
			putEnvironment(processBuilder, "REMOTE_PORT", "" + httpExchange.getRemoteAddress().getPort());
			putEnvironment(processBuilder, "REQUEST_METHOD", httpExchange.getRequestMethod());
			putEnvironment(processBuilder, "REQUEST_URI", httpExchange.getRequestURI().getPath());
			putEnvironment(processBuilder, "DOCUMENT_ROOT", cgiSettings.documentRoot.getAbsolutePath());
			putEnvironment(processBuilder, "SCRIPT_FILENAME", scriptFile.getAbsolutePath());
			putEnvironment(processBuilder, "SCRIPT_NAME", httpExchange.getRequestURI().getPath());
			putEnvironment(processBuilder, "SERVER_NAME", "" + cgiSettings.name);
			putEnvironment(processBuilder, "SERVER_PORT", "" + cgiSettings.port);
			putEnvironment(processBuilder, "SERVER_PROTOCOL", "HTTP/1.1");
			putEnvironment(processBuilder, "SERVER_SOFTWARE", cgiSettings.software);
		}

		return processBuilder;
	}

	private void putEnvironment(ProcessBuilder processBuilder, String key, String value)
	{
		if (value == null) value = "";
		processBuilder.environment().put(key, value);
	}

	private Struct2<byte[], Integer> doProcess(ProcessBuilder processBuilder, Struct2<byte[], Integer> requestBuffer) throws HTTPResponse
	{

		// プロセス開始
		Process process;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			logger.accept(e);
			throw HTTPResponse.get(500);
		}

		Struct1<HTTPResponse> connectionError = new Struct1<>();
		Struct1<Struct2<byte[], Integer>> responseBuffer = new Struct1<>();

		// 標準入力に入れるスレッド
		Thread threadIn = new Thread(() -> {
			try (OutputStream out = process.getOutputStream()) {
				out.write(requestBuffer.x, 0, requestBuffer.y);
			} catch (ClosedByInterruptException e) {

			} catch (IOException e) {
				logger.accept(e);
				connectionError.x = HTTPResponse.get(500);
			}
		});
		threadIn.start();

		// 標準出力から出すスレッド
		Thread threadOut = new Thread(() -> {
			try {
				try (InputStream in = process.getInputStream()) {
					responseBuffer.x = cgiSettings.readResponse(in, logger);
				} catch (IOException e) {
					logger.accept(e);
					connectionError.x = HTTPResponse.get(500);
				}
			} catch (HTTPResponse e) {
				connectionError.x = e;
			}
		});
		threadOut.start();

		// エラー出力から出すスレッド
		Thread threadErr = new Thread(() -> {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				while (true) {
					try {
						String line = in.readLine();
						if (line == null) break;
						logger.accept(line);
					} catch (ClosedByInterruptException e) {

					} catch (IOException e) {
						logger.accept(e);
						connectionError.x = HTTPResponse.get(500);
						return;
					}
				}
			} catch (ClosedByInterruptException e) {

			} catch (IOException e) {
				logger.accept(e);
				connectionError.x = HTTPResponse.get(500);
			}
		});
		threadErr.start();

		// プロセス完了待ち
		try {
			process.waitFor(cgiSettings.timeoutMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			connectionError.x = HTTPResponse.get(504);
		}
		if (process.isAlive()) process.destroyForcibly();

		// スレッド終了
		try {
			threadIn.join();
		} catch (InterruptedException e) {
			logger.accept(e);
			connectionError.x = HTTPResponse.get(500);
		}
		try {
			threadOut.join();
		} catch (InterruptedException e) {
			logger.accept(e);
			connectionError.x = HTTPResponse.get(500);
		}
		try {
			threadErr.join();
		} catch (InterruptedException e) {
			logger.accept(e);
			connectionError.x = HTTPResponse.get(500);
		}

		// result
		if (responseBuffer.x == null) {
			logger.accept(new NullPointerException());
			if (connectionError.x == null) connectionError.x = HTTPResponse.get(500);
		}
		if (connectionError.x != null) throw connectionError.x;
		return responseBuffer.x;
	}

	private Tuple<String, Integer> splitResponse(Struct2<byte[], Integer> responseBuffer) throws HTTPResponse
	{
		int s = responseBuffer.y;
		for (int i = 0; i < s; i++) {
			if (responseBuffer.x[i] == '\r') {
				if (i + 1 < s && responseBuffer.x[i + 1] == '\r') {
					return new Tuple<>(new String(responseBuffer.x, 0, i), i + 2);
				}
			}
			if (responseBuffer.x[i] == '\n') {
				if (i + 1 < s && responseBuffer.x[i + 1] == '\n') {
					return new Tuple<>(new String(responseBuffer.x, 0, i), i + 2);
				}
			}
			if (responseBuffer.x[i] == '\r') {
				if (i + 1 < s && responseBuffer.x[i + 1] == '\n') {
					if (i + 2 < s && responseBuffer.x[i + 2] == '\r') {
						if (i + 3 < s && responseBuffer.x[i + 3] == '\n') {
							return new Tuple<>(new String(responseBuffer.x, 0, i), i + 4);
						}
					}
				}
			}
		}
		throw HTTPResponse.get(500);
	}

	private int parseHeader(String header)
	{
		int code = 200;
		String[] lines = header.split("\n|\r\n?");

		for (String line : lines) {
			Matcher m = Pattern.compile("([^:]*)[ \t]*:[ \t]*(.*)").matcher(line);
			if (m.matches()) {
				// ヘッダ行

				httpExchange.getResponseHeaders().add(m.group(1), m.group(2));
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
		}

		return code;
	}

}
