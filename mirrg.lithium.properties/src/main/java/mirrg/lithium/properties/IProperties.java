package mirrg.lithium.properties;

import java.util.Optional;
import java.util.function.Function;

public interface IProperties
{

	public Optional<String> getString(String key);

	public default String get(String key)
	{
		return getString(key).get();
	}

	public default <T> Optional<T> getNumber(String key, Function<String, T> function)
	{
		try {
			return getString(key).map(s -> function.apply(s));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public default Optional<Byte> getByte(String key)
	{
		return getNumber(key, s -> Byte.parseByte(s, 10));
	}

	public default Optional<Short> getShort(String key)
	{
		return getNumber(key, s -> Short.parseShort(s, 10));
	}

	public default Optional<Integer> getInteger(String key)
	{
		return getNumber(key, s -> Integer.parseInt(s, 10));
	}

	public default Optional<Long> getLong(String key)
	{
		return getNumber(key, s -> Long.parseLong(s, 10));
	}

	public default Optional<Float> getFloat(String key)
	{
		return getNumber(key, s -> Float.parseFloat(s));
	}

	public default Optional<Double> getDouble(String key)
	{
		return getNumber(key, s -> Double.parseDouble(s));
	}

	public default Optional<Boolean> getBoolean(String key)
	{
		return getString(key).map(Boolean::parseBoolean);
	}

	public default Optional<Character> getChar(String key)
	{
		return getString(key).filter(s -> !s.isEmpty()).map(s -> s.charAt(0));
	}

}
