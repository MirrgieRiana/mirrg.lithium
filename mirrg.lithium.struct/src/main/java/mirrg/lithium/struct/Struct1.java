package mirrg.lithium.struct;

public class Struct1<X>
{

	public X x;

	public Struct1()
	{

	}

	public Struct1(X x)
	{
		this.x = x;
	}

	public X getX()
	{
		return x;
	}

	public void setX(X x)
	{
		this.x = x;
	}

	@Override
	public String toString()
	{
		return "[" + x + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Struct1<?> other = (Struct1<?>) obj;
		if (x == null) {
			if (other.x != null) return false;
		} else if (!x.equals(other.x)) return false;
		return true;
	}

}
