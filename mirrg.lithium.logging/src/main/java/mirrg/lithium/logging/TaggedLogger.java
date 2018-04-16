package mirrg.lithium.logging;

import java.util.Optional;

public class TaggedLogger
{

	private String tag;
	private LogSink logSink;

	public TaggedLogger(String tag, LogSink logSink)
	{
		this.tag = tag;
		this.logSink = logSink;
	}

	public void println(String string, Optional<EnumLogLevel> oLogLevel)
	{
		logSink.println(tag, string, oLogLevel);
	}

	public void println(String string)
	{
		println(string, Optional.empty());
	}

	public void println(String string, EnumLogLevel logLevel)
	{
		println(string, Optional.of(logLevel));
	}

	public void fatal(String string)
	{
		println(string, EnumLogLevel.FATAL);
	}

	public void error(String string)
	{
		println(string, EnumLogLevel.ERROR);
	}

	public void warn(String string)
	{
		println(string, EnumLogLevel.WARN);
	}

	public void info(String string)
	{
		println(string, EnumLogLevel.INFO);
	}

	public void debug(String string)
	{
		println(string, EnumLogLevel.DEBUG);
	}

	public void trace(String string)
	{
		println(string, EnumLogLevel.TRACE);
	}

	public void println(Throwable e, Optional<EnumLogLevel> oLogLevel)
	{
		logSink.println(tag, e, oLogLevel);
	}

	public void println(Throwable e)
	{
		println(e, Optional.empty());
	}

	public void println(Throwable e, EnumLogLevel logLevel)
	{
		println(e, Optional.of(logLevel));
	}

	public void fatal(Throwable e)
	{
		println(e, EnumLogLevel.FATAL);
	}

	public void error(Throwable e)
	{
		println(e, EnumLogLevel.ERROR);
	}

	public void warn(Throwable e)
	{
		println(e, EnumLogLevel.WARN);
	}

	public void info(Throwable e)
	{
		println(e, EnumLogLevel.INFO);
	}

	public void debug(Throwable e)
	{
		println(e, EnumLogLevel.DEBUG);
	}

	public void trace(Throwable e)
	{
		println(e, EnumLogLevel.TRACE);
	}

}
