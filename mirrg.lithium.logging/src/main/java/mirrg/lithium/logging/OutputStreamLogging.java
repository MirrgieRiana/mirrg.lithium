package mirrg.lithium.logging;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class OutputStreamLogging extends OutputStreamDecodeBase
{

	private String tag;
	private LogSink logger;
	private Optional<EnumLogLevel> oLogLevel = Optional.empty();

	public OutputStreamLogging(String tag, LogSink logger)
	{
		super();
		this.tag = tag;
		this.logger = logger;
	}

	public OutputStreamLogging(String tag, LogSink logger, String charset)
	{
		super(charset);
		this.tag = tag;
		this.logger = logger;
	}

	public OutputStreamLogging(String tag, LogSink logger, Charset charset)
	{
		super(charset);
		this.tag = tag;
		this.logger = logger;
	}

	public void setLogLevel(EnumLogLevel loglevel)
	{
		oLogLevel = Optional.ofNullable(loglevel);
	}

	@Override
	protected void println(String string) throws IOException
	{
		logger.println(tag, string, oLogLevel);
	}

}
