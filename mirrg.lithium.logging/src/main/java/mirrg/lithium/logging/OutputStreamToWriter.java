package mirrg.lithium.logging;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

public class OutputStreamToWriter extends OutputStreamDecodeBase
{

	private Writer writer;

	public OutputStreamToWriter(Writer writer)
	{
		super();
		this.writer = writer;
	}

	public OutputStreamToWriter(Writer writer, String charset)
	{
		super(charset);
		this.writer = writer;
	}

	public OutputStreamToWriter(Writer writer, Charset charset)
	{
		super(charset);
		this.writer = writer;
	}

	@Override
	protected void println(String string) throws IOException
	{
		writer.write(string + System.lineSeparator());
	}

}
