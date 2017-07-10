package mirrg.lithium.struct;

public final class Tuple0
{

	@Override
	public String toString()
	{
		return "[]";
	}

	@Override
	public int hashCode()
	{
		return 1;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		return true;
	}

}
