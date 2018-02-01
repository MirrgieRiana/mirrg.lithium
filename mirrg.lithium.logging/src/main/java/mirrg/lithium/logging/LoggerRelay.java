package mirrg.lithium.logging;

import java.util.ArrayList;
import java.util.Optional;

public class LoggerRelay extends Logger
{

	private ArrayList<Logger> loggers = new ArrayList<>();

	public LoggerRelay(Logger... loggers)
	{
		for (Logger logger : loggers) {
			this.loggers.add(logger);
		}
	}

	public LoggerRelay addLogger(Logger logger)
	{
		loggers.add(logger);
		return this;
	}

	@Override
	public void println(String string, Optional<EnumLogLevel> oLogLevel)
	{
		for (Logger logger : loggers) {
			logger.println(string, oLogLevel);
		}
	}

	@Override
	public void println(Throwable e, Optional<EnumLogLevel> oLogLevel)
	{
		for (Logger logger : loggers) {
			logger.println(e, oLogLevel);
		}
	}

}
