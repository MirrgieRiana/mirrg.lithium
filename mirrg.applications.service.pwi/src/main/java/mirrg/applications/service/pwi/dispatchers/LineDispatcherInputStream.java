package mirrg.applications.service.pwi.dispatchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InterruptedIOException;

import mirrg.applications.service.pw2.Logger;
import mirrg.applications.service.pwi.core.ILineReceiver;
import mirrg.applications.service.pwi.core.Line;
import mirrg.applications.service.pwi.core.LineSource;

public class LineDispatcherInputStream extends LineDispatcherBase
{

	private Logger logger;
	private ILineReceiver receiver;
	private LineSource source;
	private BufferedReader in;

	public LineDispatcherInputStream(Logger logger, BufferedReader in, LineSource source, ILineReceiver receiver)
	{
		this.logger = logger;
		this.in = in;
		this.source = source;
		this.receiver = receiver;
	}

	@Override
	protected Thread createThread() throws Exception
	{
		return new Thread(() -> {

			while (true) {

				String text;
				try {
					text = in.readLine();
				} catch (InterruptedIOException e) {
					break;
				} catch (IOException e) {
					logger.log(e);
					break;
				}

				if (text == null) break;

				receiver.onLine(logger, new Line(source, text));

			}

			receiver.onClosed(logger, source);

		}, source.name);
	}

}
