package mirrg.lithium.logging;

import java.io.PrintWriter;

public class LoggerPrintWriter implements ILogger
{

	private PrintWriter out;

	public LoggerPrintWriter(PrintWriter out)
	{
		this.out = out;
	}

	@Override
	public void println(String string, EnumLogLevel logLevel)
	{
		out.write(String.format("%-7s %s" + System.lineSeparator(),
			"[" + logLevel.name() + "]",
			string));
	}

}
