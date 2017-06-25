package mirrg.applications.service.pwi;

import java.util.LinkedList;

public class LineBuffer implements ILineReceiver
{

	private LinkedList<Line> lines = new LinkedList<>();

	@Override
	public synchronized void onLine(Line line)
	{
		lines.addLast(line);
		notifyAll();
	}

	public synchronized LinkedList<Line> flush()
	{
		LinkedList<Line> lines2 = lines;
		lines = new LinkedList<>();
		return lines2;
	}

}
