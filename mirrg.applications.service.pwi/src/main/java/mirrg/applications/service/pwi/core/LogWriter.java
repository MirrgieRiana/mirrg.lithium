package mirrg.applications.service.pwi.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class LogWriter implements AutoCloseable
{

	private PrintStream out;
	public String encoding = null;

	public synchronized void setFile(File file) throws FileNotFoundException, UnsupportedEncodingException
	{
		close();
		if (encoding != null) {
			out = new PrintStream(new FileOutputStream(file), true, encoding);
		} else {
			out = new PrintStream(new FileOutputStream(file), true);
		}
	}

	public synchronized void println(String line)
	{
		out.println(line);
	}

	@Override
	public synchronized void close()
	{
		if (out != null) out.close();
	}

}
