package mirrg.lithium.logging;

public interface ILogger
{

	public void println(String string, EnumLogLevel logLevel);

	public default void fatal(String string)
	{
		println(string, EnumLogLevel.FATAL);
	}

	public default void error(String string)
	{
		println(string, EnumLogLevel.ERROR);
	}

	public default void warn(String string)
	{
		println(string, EnumLogLevel.WARN);
	}

	public default void info(String string)
	{
		println(string, EnumLogLevel.INFO);
	}

	public default void debug(String string)
	{
		println(string, EnumLogLevel.DEBUG);
	}

	public default void trace(String string)
	{
		println(string, EnumLogLevel.TRACE);
	}

}
