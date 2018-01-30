package mirrg.lithium.logging;

import java.io.PrintStream;

public class LoggerPrintStream implements ILogger
{

	private PrintStream out;

	public LoggerPrintStream(PrintStream out)
	{
		this.out = out;
	}

	@Override
	public void println(String string, EnumLogLevel logLevel)
	{
		out.println(LoggingUtil.INSTANCE.format(string, logLevel));
	}

}
