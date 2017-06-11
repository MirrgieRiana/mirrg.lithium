package mirrg.lithium.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HLocalize
{

	public static Hashtable<String, String> table = new Hashtable<>();

	public static void register(String key, String value)
	{
		table.put(key, value);
	}

	public static void register(Map<String, String> map)
	{
		map.forEach(HLocalize::register);
	}

	public static Pattern pattern = Pattern.compile("\\s*([^=]+?)\\s*=\\s*(.*?)\\s*");

	/**
	 * 例： {@code "key=value"}
	 */
	public static void register(String line)
	{
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches()) {
			register(matcher.group(1), matcher.group(2));
		} else {
			throw new IllegalArgumentException();
		}
	}

	public static void register(InputStream in, Consumer<String> onIllegalLine) throws IOException
	{
		BufferedReader in2 = new BufferedReader(new InputStreamReader(in));

		String line;
		while ((line = in2.readLine()) != null) {
			try {
				register(line);
			} catch (IllegalArgumentException e) {
				onIllegalLine.accept(line);
			}
		}
	}

	public static void register(File file) throws IOException
	{
		try (FileInputStream in = new FileInputStream(file)) {
			register(in, line -> {
				System.err.println("illegal lang entry: '" + line + "' at " + file.getName());
			});
		}
	}

	public static void registerFromFile(String filename) throws IOException
	{
		try (FileInputStream in = new FileInputStream(filename)) {
			register(in, line -> {
				System.err.println("illegal lang entry: '" + line + "' at " + filename);
			});
		}
	}

	public static boolean isLocalizable(String key)
	{
		return table.containsKey(key);
	}

	public static String localize(String key)
	{
		return table.containsKey(key) ? table.get(key) : key;
	}

	public static String localize(String pre, String key, String suf)
	{
		return localize(pre + "." + key + "." + suf);
	}

	public static String localize(String pre, String key)
	{
		return localize(pre, key, "name");
	}

}
