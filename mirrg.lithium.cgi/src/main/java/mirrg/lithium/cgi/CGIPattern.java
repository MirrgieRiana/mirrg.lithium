package mirrg.lithium.cgi;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;

public class CGIPattern implements ICGIPattern
{

	public final String fileNameSuffix;
	/**
	 * "%s"の部分がファイル名で置換
	 */
	public final String[] commandFormat;
	public final ILogger logger;
	public final int counterLimit;

	public CGIPattern(String fileNameSuffix, String[] commandFormat, ILogger logger, int counterLimit)
	{
		this.fileNameSuffix = fileNameSuffix;
		this.commandFormat = commandFormat;
		this.logger = logger;
		this.counterLimit = counterLimit;
	}

	@Override
	public boolean isCGIScript(File file)
	{
		return file.getName().endsWith(fileNameSuffix);
	}

	@Override
	public HTTPResponse onCGI(String path, CGIServerSetting cgiServerSetting, File file, String pathInfo)
	{
		return new HTTPResponse() {
			@Override
			public void sendResponse(HttpExchange httpExchange) throws IOException
			{
				new CGIRunner(
					cgiServerSetting,
					httpExchange,
					commandFormat,
					file,
					pathInfo.isEmpty() ? Optional.empty() : Optional.of(pathInfo),
					Optional.empty(), // TODO
					logger,
					counterLimit).run();
			}

			@Override
			public boolean isError()
			{
				return false;
			}
		};
	}

}
