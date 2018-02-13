package mirrg.lithium.groovy.properties;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PathResolverFileSystem extends PathResolverURL
{

	private static URL toURL(File file)
	{
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public PathResolverFileSystem(File file)
	{
		super(toURL(new File(".")));
	}

	@Override
	public URL getResource(String path) throws IOException
	{
		if (path.startsWith("/")) throw new IOException("Absolute path can not be accepted:" + path);
		return super.getResource(path);
	}

}
