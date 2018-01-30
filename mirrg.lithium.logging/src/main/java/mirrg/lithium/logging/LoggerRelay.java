package mirrg.lithium.logging;

import java.util.ArrayList;

public class LoggerRelay implements ILogger
{

	private ArrayList<ILogger> loggers = new ArrayList<>();

	public LoggerRelay(ILogger... loggers)
	{
		for (ILogger logger : loggers) {
			this.loggers.add(logger);
		}
	}

	public LoggerRelay addLogger(ILogger logger)
	{
		loggers.add(logger);
		return this;
	}

	@Override
	public void println(String string, EnumLogLevel logLevel)
	{
		for (ILogger logger : loggers) {
			logger.println(string, logLevel);
		}
	}

	@Override
	public void fatal(String string)
	{
		for (ILogger logger : loggers) {
			logger.fatal(string);
		}
	}

	@Override
	public void error(String string)
	{
		for (ILogger logger : loggers) {
			logger.error(string);
		}
	}

	@Override
	public void warn(String string)
	{
		for (ILogger logger : loggers) {
			logger.warn(string);
		}
	}

	@Override
	public void info(String string)
	{
		for (ILogger logger : loggers) {
			logger.info(string);
		}
	}

	@Override
	public void debug(String string)
	{
		for (ILogger logger : loggers) {
			logger.debug(string);
		}
	}

	@Override
	public void trace(String string)
	{
		for (ILogger logger : loggers) {
			logger.trace(string);
		}
	}

}
