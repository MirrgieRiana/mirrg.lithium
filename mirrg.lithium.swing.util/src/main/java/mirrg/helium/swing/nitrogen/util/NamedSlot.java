package mirrg.helium.swing.nitrogen.util;

import java.util.function.Function;
import java.util.function.Supplier;

public class NamedSlot<T> implements Supplier<T>
{

	private T t;
	private Function<T, String> functionName;

	public NamedSlot(T t, Function<T, String> functionName)
	{
		this.t = t;
		this.functionName = functionName;
	}

	@Override
	public String toString()
	{
		return functionName.apply(t);
	}

	@Override
	public T get()
	{
		return t;
	}

}
