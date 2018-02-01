package mirrg.lithium.logging;

import java.io.PrintWriter;
import java.util.Optional;

public class LoggerPrintWriter extends Logger
{

	private PrintWriter out;
	public ILogFormatter formatter = LogFormatterSimple.INSTANCE;

	public LoggerPrintWriter(PrintWriter out)
	{
		this.out = out;
	}

	@Override
	public void println(String string, Optional<EnumLogLevel> oLogLevel)
	{
		out.println(formatter.format(string, oLogLevel));
	}

}