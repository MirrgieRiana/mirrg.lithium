package mirrg.lithium.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public abstract class Logger
{

	public abstract void println(String string, Optional<EnumLogLevel> oLogLevel);

	public final void println(String string)
	{
		println(string, Optional.empty());
	}

	public final void println(String string, EnumLogLevel logLevel)
	{
		println(string, Optional.of(logLevel));
	}

	public final void fatal(String string)
	{
		println(string, EnumLogLevel.FATAL);
	}

	public final void error(String string)
	{
		println(string, EnumLogLevel.ERROR);
	}

	public final void warn(String string)
	{
		println(string, EnumLogLevel.WARN);
	}

	public final void info(String string)
	{
		println(string, EnumLogLevel.INFO);
	}

	public final void debug(String string)
	{
		println(string, EnumLogLevel.DEBUG);
	}

	public final void trace(String string)
	{
		println(string, EnumLogLevel.TRACE);
	}

	public void println(Throwable e, Optional<EnumLogLevel> oLogLevel)
	{
		StringWriter out = new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		println(out.toString(), oLogLevel);
	}

	public final void println(Throwable e)
	{
		println(e, Optional.empty());
	}

	public final void println(Throwable e, EnumLogLevel logLevel)
	{
		println(e, Optional.of(logLevel));
	}

	public final void fatal(Throwable e)
	{
		println(e, EnumLogLevel.FATAL);
	}

	public final void error(Throwable e)
	{
		println(e, EnumLogLevel.ERROR);
	}

	public final void warn(Throwable e)
	{
		println(e, EnumLogLevel.WARN);
	}

	public final void info(Throwable e)
	{
		println(e, EnumLogLevel.INFO);
	}

	public final void debug(Throwable e)
	{
		println(e, EnumLogLevel.DEBUG);
	}

	public final void trace(Throwable e)
	{
		println(e, EnumLogLevel.TRACE);
	}

}
