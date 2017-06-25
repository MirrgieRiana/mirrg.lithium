package mirrg.applications.service.pw2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.lithium.struct.Tuple;

public class LineStorage implements ILineReceiver
{

	private int size;
	private LinkedList<Tuple<Integer, Line>> lines = new LinkedList<>();
	private int count = 0;

	public LineStorage(int size)
	{
		this.size = size;
	}

	@Override
	public synchronized void onLine(Line line)
	{
		lines.addLast(new Tuple<>(count, line));
		if (size >= 0 && lines.size() > size) lines.removeFirst();
		count++;
		notifyAll();
	}

	public synchronized int getCount()
	{
		return count;
	}

	public synchronized Stream<Tuple<Integer, Line>> stream()
	{
		ArrayList<Tuple<Integer, Line>> lines2 = lines.stream().collect(Collectors.toCollection(ArrayList::new));
		return lines2.stream();
	}

}
