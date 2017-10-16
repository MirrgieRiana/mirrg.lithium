package mirrg.lithium.objectduct.logging;

import java.util.function.Supplier;

import org.apache.commons.logging.Log;

public class LoggerLater
{

	public final Log log;

	public LoggerLater(Log log)
	{
		this.log = log;
	}

	public void fatal(Supplier<Object> sMessage)
	{
		if (log.isFatalEnabled()) log.fatal(sMessage.get());
	}

	public void error(Supplier<Object> sMessage)
	{
		if (log.isErrorEnabled()) log.error(sMessage.get());
	}

	public void warn(Supplier<Object> sMessage)
	{
		if (log.isWarnEnabled()) log.warn(sMessage.get());
	}

	public void info(Supplier<Object> sMessage)
	{
		if (log.isInfoEnabled()) log.info(sMessage.get());
	}

	public void debug(Supplier<Object> sMessage)
	{
		if (log.isDebugEnabled()) log.debug(sMessage.get());
	}

	public void trace(Supplier<Object> sMessage)
	{
		if (log.isTraceEnabled()) log.trace(sMessage.get());
	}

	public void fatal(Supplier<Object> sMessage, Supplier<Throwable> sT)
	{
		if (log.isFatalEnabled()) log.fatal(sMessage.get(), sT.get());
	}

	public void error(Supplier<Object> sMessage, Supplier<Throwable> sT)
	{
		if (log.isErrorEnabled()) log.error(sMessage.get(), sT.get());
	}

	public void warn(Supplier<Object> sMessage, Supplier<Throwable> sT)
	{
		if (log.isWarnEnabled()) log.warn(sMessage.get(), sT.get());
	}

	public void info(Supplier<Object> sMessage, Supplier<Throwable> sT)
	{
		if (log.isInfoEnabled()) log.info(sMessage.get(), sT.get());
	}

	public void debug(Supplier<Object> sMessage, Supplier<Throwable> sT)
	{
		if (log.isDebugEnabled()) log.debug(sMessage.get(), sT.get());
	}

	public void trace(Supplier<Object> sMessage, Supplier<Throwable> sT)
	{
		if (log.isTraceEnabled()) log.trace(sMessage.get(), sT.get());
	}

}
