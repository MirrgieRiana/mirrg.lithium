package mirrg.applications.service.pwi.dispatchers;

import mirrg.applications.service.pwi.core.ILineDispatcher;

public abstract class LineDispatcherThreadBase implements ILineDispatcher
{

	private Thread thread;

	protected abstract Thread createThread() throws Exception;

	@Override
	public ILineDispatcher start(boolean isDaemon) throws Exception
	{
		thread = createThread();
		if (isDaemon) thread.setDaemon(true);
		thread.start();
		return this;
	}

	@Override
	public void stop() throws Exception
	{
		thread.interrupt();
		thread.join();
	}

}
