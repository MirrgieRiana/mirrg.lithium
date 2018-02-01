package mirrg.lithium.lang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class HFile
{

	public static FileOutputStream getOutputStreamAndMkdirs(File file) throws FileNotFoundException
	{
		file.getAbsoluteFile().getParentFile().mkdirs();
		return new FileOutputStream(file);
	}

}
