package mirrg.lithium.logging;

import java.io.PrintStream;
import java.util.Optional;

public class LoggerPrintStream extends Logger
{

	private PrintStream out;
	public ILogFormatter formatter = LogFormatterSimple.INSTANCE;

	public LoggerPrintStream(PrintStream out)
	{
		this.out = out;
	}

	@Override
	public void println(String string, Optional<EnumLogLevel> oLogLevel)
	{
		out.println(formatter.format(string, oLogLevel));
	}

}
