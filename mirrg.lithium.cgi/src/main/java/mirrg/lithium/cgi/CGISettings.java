package mirrg.lithium.cgi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;

import mirrg.lithium.struct.ImmutableArray;
import mirrg.lithium.struct.Struct2;

public class CGISettings
{

	public final String name;
	public final int port;
	public final String software;
	public final File documentRoot;
	public final ImmutableArray<String> command;
	public final int timeoutMs;
	private Struct2<byte[], Integer> requestBuffer = new Struct2<>();
	private Struct2<byte[], Integer> responseBuffer = new Struct2<>();

	public CGISettings(
		String name,
		int port,
		String software,
		File documentRoot,
		ImmutableArray<String> command,
		int timeoutMs,
		int requestBufferSize,
		int responseBufferSize)
	{
		this.name = name;
		this.port = port;
		this.software = software;
		this.documentRoot = documentRoot;
		this.command = command;
		this.timeoutMs = timeoutMs;
		this.requestBuffer.x = new byte[requestBufferSize];
		this.requestBuffer.y = 0;
		this.responseBuffer.x = new byte[responseBufferSize];
		this.responseBuffer.y = 0;
	}

	public Struct2<byte[], Integer> readRequest(InputStream in, ILogger logger) throws HTTPResponse
	{
		try {
			try {
				requestBuffer.y = in.read(requestBuffer.x);
				if (requestBuffer.y == -1) requestBuffer.y = 0;
				if (in.read() == -1) {
					return requestBuffer;
				} else {
					throw HTTPResponse.get(413, "413: Too large request");
				}
			} catch (IOException e) {
				logger.accept(e);
				throw HTTPResponse.get(400);
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.accept(e);
				throw HTTPResponse.get(500);
			}
		}
	}

	public Struct2<byte[], Integer> readResponse(InputStream in, ILogger logger) throws HTTPResponse
	{
		try {
			try {
				responseBuffer.y = in.read(responseBuffer.x);
				if (responseBuffer.y == -1) responseBuffer.y = 0;
				if (in.read() == -1) {
					return responseBuffer;
				} else {
					throw HTTPResponse.get(500, "500: Buffer overflow");
				}
			} catch (IOException e) {
				logger.accept(e);
				throw HTTPResponse.get(500);
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.accept(e);
				throw HTTPResponse.get(500);
			}
		}
	}

	public String[] createCommand(File scriptFile)
	{
		return command.stream()
			.map(s -> s.replace("%s", scriptFile.getAbsolutePath()))
			.toArray(String[]::new);
	}

	public void doCGI(
		HttpExchange httpExchange,
		File scriptFile,
		Optional<String> oPathInfo,
		Optional<String> oPathTranslated,
		ILogger logger)
	{
		new CGIRunner(
			this,
			httpExchange,
			scriptFile,
			oPathInfo,
			oPathTranslated,
			logger,
			1000).doCGI();
	}

}
