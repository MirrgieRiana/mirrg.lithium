package mirrg.lithium.struct;

import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class ImmutableArray<T>
{

	private final T[] array;

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public ImmutableArray(T... array)
	{
		this.array = (T[]) new Object[array.length];
		Class<?> clazz = array.getClass().getComponentType();
		for (int i = 0; i < array.length; i++) {
			if (!clazz.isInstance(array[i])) throw new ClassCastException();
			this.array[i] = array[i];
		}
	}

	@SuppressWarnings("unchecked")
	public ImmutableArray(List<T> array)
	{
		this.array = (T[]) new Object[array.size()];
		for (int i = 0; i < array.size(); i++) {
			this.array[i] = array.get(i);
		}
	}

	public int length()
	{
		return array.length;
	}

	public T get(int index)
	{
		return array[index];
	}

	public Enumeration<T> values()
	{
		return new Enumeration<T>() {
			private int i = 0;

			@Override
			public boolean hasMoreElements()
			{
				return i < array.length;
			}

			@Override
			public T nextElement()
			{
				return array[i++];
			}
		};
	}

	public Stream<T> stream()
	{
		return Stream.of(array);
	}

	public void forEach(Consumer<T> consumer)
	{
		for (int i = 0; i < array.length; i++) {
			consumer.accept(array[i]);
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < array.length; i++) {
			if (i != 0) sb.append(", ");
			sb.append(array[i].toString());
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		for (int i = 0; i < array.length; i++) {
			result = prime * result + ((array[i] == null) ? 0 : array[i].hashCode());
		}
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ImmutableArray<?> other = (ImmutableArray<?>) obj;
		if (array.length != other.array.length) return false;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null) {
				if (other.array[i] != null) return false;
			} else if (!array[i].equals(other.array[i])) return false;
		}
		return true;
	}

}
