package mirrg.lithium.struct;

public class Tuple3<X, Y, Z>
{

	public final X x;
	public final Y y;
	public final Z z;

	public Tuple3(X x, Y y, Z z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public X getX()
	{
		return x;
	}

	public Tuple3<X, Y, Z> deriveX(X x)
	{
		return new Tuple3<>(x, y, z);
	}

	public Y getY()
	{
		return y;
	}

	public Tuple3<X, Y, Z> deriveY(Y y)
	{
		return new Tuple3<>(x, y, z);
	}

	public Z getZ()
	{
		return z;
	}

	public Tuple3<X, Y, Z> deriveZ(Z z)
	{
		return new Tuple3<>(x, y, z);
	}

	@Override
	public String toString()
	{
		return "[" + x + ", " + y + ", " + z + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		result = prime * result + ((z == null) ? 0 : z.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Tuple3<?, ?, ?> other = (Tuple3<?, ?, ?>) obj;
		if (x == null) {
			if (other.x != null) return false;
		} else if (!x.equals(other.x)) return false;
		if (y == null) {
			if (other.y != null) return false;
		} else if (!y.equals(other.y)) return false;
		if (z == null) {
			if (other.z != null) return false;
		} else if (!z.equals(other.z)) return false;
		return true;
	}

}
