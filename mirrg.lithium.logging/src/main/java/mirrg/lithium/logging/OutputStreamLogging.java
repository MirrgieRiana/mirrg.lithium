package mirrg.lithium.logging;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class OutputStreamLogging extends OutputStreamDecodeBase
{

	private Logger logger;
	private Optional<EnumLogLevel> oLogLevel = Optional.empty();

	public OutputStreamLogging(Logger logger)
	{
		super();
		this.logger = logger;
	}

	public OutputStreamLogging(Logger logger, String charset)
	{
		super(charset);
		this.logger = logger;
	}

	public OutputStreamLogging(Logger logger, Charset charset)
	{
		super(charset);
		this.logger = logger;
	}

	public void setLogLevel(EnumLogLevel loglevel)
	{
		oLogLevel = Optional.ofNullable(loglevel);
	}

	@Override
	protected void println(String string) throws IOException
	{
		logger.println(string, oLogLevel);
	}

}
