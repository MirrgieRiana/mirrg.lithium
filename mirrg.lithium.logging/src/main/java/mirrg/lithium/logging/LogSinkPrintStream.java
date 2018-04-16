package mirrg.lithium.logging;

import java.io.PrintStream;
import java.util.Optional;

public class LogSinkPrintStream extends LogSink
{

	private PrintStream out;
	public ILogFormatter formatter = LogFormatterSimple.INSTANCE;

	public LogSinkPrintStream(PrintStream out)
	{
		this.out = out;
	}

	@Override
	public void println(String tag, String string, Optional<EnumLogLevel> oLogLevel)
	{
		out.println(formatter.format(tag, string, oLogLevel));
	}

}
