package mirrg.lithium.properties;

import java.io.File;

public class PropertiesContext
{

	public final Properties properties;
	public final File currentDirectory;

	public PropertiesContext(Properties properties, File currentDirectory)
	{
		this.properties = properties;
		this.currentDirectory = currentDirectory;
	}

}
