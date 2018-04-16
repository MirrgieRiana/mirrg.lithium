package mirrg.lithium.logging;

import java.util.Optional;

public class Logger
{

	private LogSink logSink;

	public Logger(LogSink logSink)
	{
		this.logSink = logSink;
	}

	public void println(String tag, String string, Optional<EnumLogLevel> oLogLevel)
	{
		logSink.println(tag, string, oLogLevel);
	}

	public void println(String tag, String string)
	{
		println(tag, string, Optional.empty());
	}

	public void println(String tag, String string, EnumLogLevel logLevel)
	{
		println(tag, string, Optional.of(logLevel));
	}

	public void fatal(String tag, String string)
	{
		println(tag, string, EnumLogLevel.FATAL);
	}

	public void error(String tag, String string)
	{
		println(tag, string, EnumLogLevel.ERROR);
	}

	public void warn(String tag, String string)
	{
		println(tag, string, EnumLogLevel.WARN);
	}

	public void info(String tag, String string)
	{
		println(tag, string, EnumLogLevel.INFO);
	}

	public void debug(String tag, String string)
	{
		println(tag, string, EnumLogLevel.DEBUG);
	}

	public void trace(String tag, String string)
	{
		println(tag, string, EnumLogLevel.TRACE);
	}

	public void println(String tag, Throwable e, Optional<EnumLogLevel> oLogLevel)
	{
		logSink.println(tag, e, oLogLevel);
	}

	public void println(String tag, Throwable e)
	{
		println(tag, e, Optional.empty());
	}

	public void println(String tag, Throwable e, EnumLogLevel logLevel)
	{
		println(tag, e, Optional.of(logLevel));
	}

	public void fatal(String tag, Throwable e)
	{
		println(tag, e, EnumLogLevel.FATAL);
	}

	public void error(String tag, Throwable e)
	{
		println(tag, e, EnumLogLevel.ERROR);
	}

	public void warn(String tag, Throwable e)
	{
		println(tag, e, EnumLogLevel.WARN);
	}

	public void info(String tag, Throwable e)
	{
		println(tag, e, EnumLogLevel.INFO);
	}

	public void debug(String tag, Throwable e)
	{
		println(tag, e, EnumLogLevel.DEBUG);
	}

	public void trace(String tag, Throwable e)
	{
		println(tag, e, EnumLogLevel.TRACE);
	}

}
