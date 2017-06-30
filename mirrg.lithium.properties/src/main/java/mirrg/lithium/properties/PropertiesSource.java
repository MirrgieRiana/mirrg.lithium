package mirrg.lithium.properties;

import java.io.File;
import java.util.Optional;

public class PropertiesSource
{

	public final File directory;
	public final Optional<File> oFile;
	public final String sourceName;

	public PropertiesSource(File file)
	{
		this(file.getAbsoluteFile().getParentFile(), Optional.of(file), file.getName());
	}

	public PropertiesSource(File directory, String sourceName)
	{
		this(directory, Optional.empty(), sourceName);
	}

	public PropertiesSource(File directory, Optional<File> oFile, String sourceName)
	{
		this.directory = directory;
		this.oFile = oFile;
		this.sourceName = sourceName;
	}

}
