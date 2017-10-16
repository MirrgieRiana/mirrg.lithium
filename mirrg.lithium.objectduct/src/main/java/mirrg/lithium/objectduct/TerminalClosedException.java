package mirrg.lithium.objectduct;

public class TerminalClosedException extends Exception
{

	public TerminalClosedException()
	{
		super();
	}

	public TerminalClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TerminalClosedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TerminalClosedException(String message)
	{
		super(message);
	}

	public TerminalClosedException(Throwable cause)
	{
		super(cause);
	}

}
