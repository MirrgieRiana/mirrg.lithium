package mirrg.lithium.properties;

import java.util.Optional;
import java.util.function.Function;

public interface IProperty
{

	public Optional<String> getString();

	public default <T> Optional<T> getNumber(Function<String, T> function)
	{
		try {
			return getString().map(s -> function.apply(s));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public default Optional<Byte> getByte()
	{
		return getNumber(s -> Byte.parseByte(s, 10));
	}

	public default Optional<Short> getShort()
	{
		return getNumber(s -> Short.parseShort(s, 10));
	}

	public default Optional<Integer> getInteger()
	{
		return getNumber(s -> Integer.parseInt(s, 10));
	}

	public default Optional<Long> getLong()
	{
		return getNumber(s -> Long.parseLong(s, 10));
	}

	public default Optional<Float> getFloat()
	{
		return getNumber(s -> Float.parseFloat(s));
	}

	public default Optional<Double> getDouble()
	{
		return getNumber(s -> Double.parseDouble(s));
	}

	public default Optional<Boolean> getBoolean()
	{
		return getString().map(Boolean::parseBoolean);
	}

	public default Optional<Character> getChar()
	{
		return getString().filter(s -> !s.isEmpty()).map(s -> s.charAt(0));
	}

}
