package mirrg.applications.service.pwi;

import java.io.File;
import java.util.ArrayList;

import mirrg.lithium.properties.HPropertiesParser;
import mirrg.lithium.properties.Properties;

public class Main
{

	public static void main(String[] args) throws Exception
	{
		new Launcher(parse("default.pwi.properties", args)).start();
	}

	private static Properties parse(String defaultPropertyFileName, String... args) throws Exception
	{
		boolean useDefaultPropertyFile = true;

		Properties properties = new Properties();
		{
			for (String arg : args) {

				// =が入ってたら値
				int index = arg.indexOf("=");
				if (index >= 0) {
					properties.put(arg.substring(0, index), arg.substring(index + 1));
					continue;
				}

				// 空じゃなかったら親
				if (!arg.isEmpty()) {

					ArrayList<Exception> exceptions = new ArrayList<>();
					properties.addParent(HPropertiesParser.parse(new File(arg), exceptions::add));
					if (!exceptions.isEmpty()) throw exceptions.get(0);

					useDefaultPropertyFile = false;
					continue;
				}

				throw new RuntimeException();

			}
		}

		if (useDefaultPropertyFile) {
			ArrayList<Exception> exceptions = new ArrayList<>();
			properties.addParent(HPropertiesParser.parse(new File(defaultPropertyFileName), exceptions::add));
			if (!exceptions.isEmpty()) throw exceptions.get(0);
		}

		return properties;
	}

}
