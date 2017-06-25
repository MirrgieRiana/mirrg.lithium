package mirrg.applications.service.pwi;

public abstract class BlockLineBufferBase extends BlockBase
{

	private LineBuffer in;

	public BlockLineBufferBase(Logger logger, LineBuffer in) throws Exception
	{
		super(logger);
		this.in = in;
	}

	@Override
	protected Thread createThread() throws Exception
	{
		return new Thread(() -> {

			while (true) {

				synchronized (in) {

					in.flush().forEach(l -> {
						try {
							onLine(l);
						} catch (Exception e) {
							logger.log(e);
						}
					});

					try {
						in.wait();
					} catch (InterruptedException e) {
						break;
					}
				}

			}

		}, getThreadName());
	}

	protected abstract void onLine(Line line) throws Exception;

	protected String getThreadName()
	{
		return getClass().getSimpleName();
	}

}
