package mirrg.applications.service.pwi.core;

public interface ILineDispatcher extends AutoCloseable
{

	public ILineDispatcher start(boolean isDaemon) throws Exception;

	public default ILineDispatcher start() throws Exception
	{
		return start(true);
	}

}
