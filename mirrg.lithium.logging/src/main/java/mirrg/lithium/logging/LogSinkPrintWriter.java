package mirrg.lithium.logging;

import java.io.PrintWriter;
import java.util.Optional;

public class LogSinkPrintWriter extends LogSink
{

	private PrintWriter out;
	public ILogFormatter formatter = LogFormatterSimple.INSTANCE;

	public LogSinkPrintWriter(PrintWriter out)
	{
		this.out = out;
	}

	@Override
	public void println(String tag, String string, Optional<EnumLogLevel> oLogLevel)
	{
		out.println(formatter.format(tag, string, oLogLevel));
	}

}
