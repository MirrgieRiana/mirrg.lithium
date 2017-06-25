package mirrg.applications.service.pw2;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BlockLineBufferRedirection extends BlockLineBufferBase
{

	public static final DateTimeFormatter FORMATTER_LOG = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

	public BlockLineBufferRedirection(Logger logger, LineBuffer in) throws Exception
	{
		super(logger, in);
	}

	@Override
	public void onLine(Line line) throws Exception
	{
		lineReceivers.forEach(c -> {
			try {
				c.onLine(line);
			} catch (Exception e) {
				logger.log(e);
			}
		});
		String text = String.format("[%s] [%s] %s", line.time.format(FORMATTER_LOG), line.source.name, line.text);
		stringReceivers.forEach(c -> {
			try {
				c.onLine(text);
			} catch (Exception e) {
				logger.log(e);
			}
		});
		rawStringReceivers.forEach(c -> {
			try {
				c.onLine(line.text);
			} catch (Exception e) {
				logger.log(e);
			}
		});
	}

	private ArrayList<ILineReceiver> lineReceivers = new ArrayList<>();
	private ArrayList<IStringReceiver> stringReceivers = new ArrayList<>();
	private ArrayList<IStringReceiver> rawStringReceivers = new ArrayList<>();

	public BlockLineBufferRedirection addLineConsumer(ILineReceiver lineReceiver)
	{
		lineReceivers.add(lineReceiver);
		return this;
	}

	public BlockLineBufferRedirection addStringConsumer(IStringReceiver stringReceiver)
	{
		stringReceivers.add(stringReceiver);
		return this;
	}

	public BlockLineBufferRedirection addRawStringConsumer(IStringReceiver rawStringReceiver)
	{
		rawStringReceivers.add(rawStringReceiver);
		return this;
	}

}
