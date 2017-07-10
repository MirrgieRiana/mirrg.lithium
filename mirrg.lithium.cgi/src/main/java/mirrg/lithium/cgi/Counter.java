package mirrg.lithium.cgi;

public class Counter
{

	private int limit;
	private int counter = 0;

	public Counter(int limit)
	{
		this.limit = limit;
	}

	public void count()
	{
		counter++;
		if (counter > limit) throw new CounterOverflowException();
	}

}
