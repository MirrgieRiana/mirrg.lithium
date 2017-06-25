package mirrg.applications.service.pwi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InterruptedIOException;

public class BlockBufferedReaderToReceiver extends BlockBase
{

	private BufferedReader in;
	private LineSource source;
	private ILineReceiver receiver;

	public BlockBufferedReaderToReceiver(Logger logger, BufferedReader in, LineSource source, ILineReceiver receiver) throws Exception
	{
		super(logger);
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

				try {
					receiver.onLine(new Line(source, text));
				} catch (Exception e) {
					logger.log(e);
				}

			}

		}, getClass().getSimpleName() + "(" + source.name + ")");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void close() throws Exception
	{
		thread.interrupt();
		thread.join(1000);
		if (thread.isAlive()) {
			try {
				thread.destroy();
			} catch (NoSuchMethodError e) {
				// 何故か発生する
			}
		}
	}

}
