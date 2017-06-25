package mirrg.applications.service.pwi.dispatchers;

public abstract class LineDispatcherBase implements AutoCloseable
{

	private Thread thread;

	public void start(boolean isDaemon) throws Exception
	{
		thread = createThread();
		if (isDaemon) thread.setDaemon(true);
		thread.start();
	}

	public void start() throws Exception
	{
		start(true);
	}

	protected abstract Thread createThread() throws Exception;

	@Override
	public void close() throws Exception
	{
		thread.interrupt();
		thread.join();
	}

}
