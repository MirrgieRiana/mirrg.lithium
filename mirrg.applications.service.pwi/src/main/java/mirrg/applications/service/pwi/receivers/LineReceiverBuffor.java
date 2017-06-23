package mirrg.applications.service.pwi.receivers;

import mirrg.applications.service.pwi.core.ILineReceiver;
import mirrg.applications.service.pwi.core.Line;
import mirrg.applications.service.pwi.core.LineBuffer;
import mirrg.applications.service.pwi.core.LineSource;

public class LineReceiverBuffor implements ILineReceiver
{

	private LineBuffer lineBuffer;

	public LineReceiverBuffor(LineBuffer lineBuffer)
	{
		this.lineBuffer = lineBuffer;
	}

	@Override
	public void onLine(Line line) throws Exception
	{
		lineBuffer.push(line);
	}

	@Override
	public void onClosed(LineSource source) throws Exception
	{

	}

}
