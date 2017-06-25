package mirrg.applications.service.pwi.dispatchers;

import mirrg.applications.service.pw2.Logger;
import mirrg.applications.service.pwi.core.ILineReceiver;
import mirrg.applications.service.pwi.core.LineBuffer;
import mirrg.applications.service.pwi.core.LineSource;

public class LineDispatcherBuffer extends LineDispatcherBase
{

	private Logger logger;
	private ILineReceiver receiver;
	private LineBuffer in;

	public LineDispatcherBuffer(Logger logger, ILineReceiver receiver, LineBuffer in)
	{
		this.logger = logger;
		this.receiver = receiver;
		this.in = in;
	}

	@Override
	protected Thread createThread() throws Exception
	{
		return new Thread(() -> {

			while (true) {

				synchronized (in) {

					in.flush().forEach(l -> receiver.onLine(logger, l));

					try {
						in.wait();
					} catch (InterruptedException e) {
						break;
					}
				}

			}

			receiver.onClosed(logger, new LineSource("BUFFER", "orange"));

		}, "BUFFER");
	}

}
