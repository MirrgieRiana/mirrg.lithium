package mirrg.applications.service.pw2;

public abstract class BlockBase implements AutoCloseable
{

	protected Logger logger;
	protected Thread thread;

	public BlockBase(Logger logger) throws Exception
	{
		this.logger = logger;
	}

	public BlockBase start(boolean isDaemon) throws Exception
	{
		thread = createThread();
		if (isDaemon) thread.setDaemon(true);
		thread.start();
		return this;
	}

	protected abstract Thread createThread() throws Exception;

	@Override
	public void close() throws Exception
	{
		thread.interrupt();
		thread.join();
	}

}
