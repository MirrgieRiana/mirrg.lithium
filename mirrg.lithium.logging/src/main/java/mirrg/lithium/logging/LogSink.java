package mirrg.lithium.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public abstract class LogSink
{

	public abstract void println(String tag, String string, Optional<EnumLogLevel> oLogLevel);

	public void println(String tag, Throwable e, Optional<EnumLogLevel> oLogLevel)
	{
		StringWriter out = new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		println(tag, out.toString(), oLogLevel);
	}

}
