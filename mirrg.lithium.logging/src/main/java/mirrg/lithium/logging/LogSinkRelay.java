package mirrg.lithium.logging;

import java.util.ArrayList;
import java.util.Optional;

public class LogSinkRelay extends LogSink
{

	private ArrayList<LogSink> logSinks = new ArrayList<>();

	public LogSinkRelay(LogSink... logSinks)
	{
		for (LogSink logSink : logSinks) {
			this.logSinks.add(logSink);
		}
	}

	public LogSinkRelay addLogSink(LogSink logSink)
	{
		logSinks.add(logSink);
		return this;
	}

	@Override
	public void println(String tag, String string, Optional<EnumLogLevel> oLogLevel)
	{
		for (LogSink logSink : logSinks) {
			logSink.println(tag, string, oLogLevel);
		}
	}

	@Override
	public void println(String tag, Throwable e, Optional<EnumLogLevel> oLogLevel)
	{
		for (LogSink logSink : logSinks) {
			logSink.println(tag, e, oLogLevel);
		}
	}

}
