package mirrg.applications.service.pw2;

import java.util.LinkedList;

public class LineBuffer implements ILineReceiver
{

	private LinkedList<Line> lines = new LinkedList<>();

	public synchronized void push(Line line)
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

	@Override
	public void onLine(Line line)
	{
		push(line);
	}

}
