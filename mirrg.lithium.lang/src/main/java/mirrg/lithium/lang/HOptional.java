package mirrg.lithium.lang;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.regex.Pattern;

public class HOptional
{

	public static Pattern PATTERN_INTEGER = Pattern.compile("[+\\-]?[0-9]+");

	public static OptionalInt parseInt(String s)
	{
		if (!PATTERN_INTEGER.matcher(s).matches()) return OptionalInt.empty();
		try {
			return OptionalInt.of(Integer.parseInt(s));
		} catch (NumberFormatException e) {
			return OptionalInt.empty();
		}
	}

	public static OptionalInt parseInt(String s, int radix)
	{
		if (!PATTERN_INTEGER.matcher(s).matches()) return OptionalInt.empty();
		try {
			return OptionalInt.of(Integer.parseInt(s, radix));
		} catch (NumberFormatException e) {
			return OptionalInt.empty();
		}
	}

	public static OptionalLong parseLong(String s)
	{
		if (!PATTERN_INTEGER.matcher(s).matches()) return OptionalLong.empty();
		try {
			return OptionalLong.of(Long.parseLong(s));
		} catch (NumberFormatException e) {
			return OptionalLong.empty();
		}
	}

	public static OptionalLong parseLong(String s, int radix)
	{
		if (!PATTERN_INTEGER.matcher(s).matches()) return OptionalLong.empty();
		try {
			return OptionalLong.of(Long.parseLong(s, radix));
		} catch (NumberFormatException e) {
			return OptionalLong.empty();
		}
	}

	public static OptionalDouble parseDouble(String s)
	{
		try {
			return OptionalDouble.of(Double.parseDouble(s));
		} catch (NumberFormatException e) {
			return OptionalDouble.empty();
		}
	}

}
