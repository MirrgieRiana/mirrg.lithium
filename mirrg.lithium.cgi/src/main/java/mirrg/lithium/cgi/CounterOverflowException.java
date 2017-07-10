package mirrg.lithium.cgi;

public class CounterOverflowException extends RuntimeException
{

	public CounterOverflowException()
	{
		super();
	}

	public CounterOverflowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CounterOverflowException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CounterOverflowException(String message)
	{
		super(message);
	}

	public CounterOverflowException(Throwable cause)
	{
		super(cause);
	}

}
