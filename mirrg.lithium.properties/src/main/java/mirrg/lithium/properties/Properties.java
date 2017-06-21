package mirrg.lithium.properties;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;
import java.util.function.Function;

public class Properties
{

	private ArrayList<Properties> parents = new ArrayList<>();
	private Hashtable<String, IMethod> methods = new Hashtable<>();

	public void addParent(Properties parent)
	{
		parents.add(parent);
	}

	public void put(String key, IMethod method)
	{
		methods.put(key, method);
	}

	public void put(String key, String value)
	{
		methods.put(key, p -> value);
	}

	public Optional<IMethod> getMethod(String key)
	{
		IMethod method = methods.get(key);
		if (method != null) return Optional.of(method);
		for (Properties parent : parents) {
			Optional<IMethod> oMethod = parent.getMethod(key);
			if (oMethod.isPresent()) return oMethod;
		}
		return Optional.empty();
	}

	public Optional<String> getString(String key)
	{
		return getMethod(key).map(m -> m.apply(this));
	}

	private <T> Optional<T> getNumber(String key, Function<String, T> function)
	{
		try {
			return getString(key).map(s -> function.apply(s));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public Optional<Byte> getByte(String key)
	{
		return getNumber(key, s -> Byte.parseByte(s, 10));
	}

	public Optional<Short> getShort(String key)
	{
		return getNumber(key, s -> Short.parseShort(s, 10));
	}

	public Optional<Integer> getInteger(String key)
	{
		return getNumber(key, s -> Integer.parseInt(s, 10));
	}

	public Optional<Long> getLong(String key)
	{
		return getNumber(key, s -> Long.parseLong(s, 10));
	}

	public Optional<Float> getFloat(String key)
	{
		return getNumber(key, s -> Float.parseFloat(s));
	}

	public Optional<Double> getDouble(String key)
	{
		return getNumber(key, s -> Double.parseDouble(s));
	}

	public Optional<Boolean> getBoolean(String key)
	{
		return getString(key).map(Boolean::parseBoolean);
	}

	public Optional<Character> getChar(String key)
	{
		return getString(key).filter(s -> !s.isEmpty()).map(s -> s.charAt(0));
	}

}
