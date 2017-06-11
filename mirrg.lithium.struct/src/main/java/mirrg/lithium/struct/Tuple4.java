package mirrg.lithium.struct;

public class Tuple4<X, Y, Z, W>
{

	public final X x;
	public final Y y;
	public final Z z;
	public final W w;

	public Tuple4(X x, Y y, Z z, W w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public X getX()
	{
		return x;
	}

	public Tuple4<X, Y, Z, W> deriveX(X x)
	{
		return new Tuple4<>(x, y, z, w);
	}

	public Y getY()
	{
		return y;
	}

	public Tuple4<X, Y, Z, W> deriveY(Y y)
	{
		return new Tuple4<>(x, y, z, w);
	}

	public Z getZ()
	{
		return z;
	}

	public Tuple4<X, Y, Z, W> deriveZ(Z z)
	{
		return new Tuple4<>(x, y, z, w);
	}

	public W getW()
	{
		return w;
	}

	public Tuple4<X, Y, Z, W> deriveW(W w)
	{
		return new Tuple4<>(x, y, z, w);
	}

	@Override
	public String toString()
	{
		return "[" + x + ", " + y + ", " + z + ", " + w + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((w == null) ? 0 : w.hashCode());
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
		Tuple4<?, ?, ?, ?> other = (Tuple4<?, ?, ?, ?>) obj;
		if (w == null) {
			if (other.w != null) return false;
		} else if (!w.equals(other.w)) return false;
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
