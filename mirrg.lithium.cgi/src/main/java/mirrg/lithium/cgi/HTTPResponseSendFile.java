package mirrg.lithium.cgi;

import java.io.File;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class HTTPResponseSendFile extends HTTPResponse
{

	public File file;

	public HTTPResponseSendFile(File file)
	{
		this.file = file;
	}

	@Override
	public void sendResponse(HttpExchange httpExchange) throws IOException
	{
		sendFile(httpExchange, file);
	}

	@Override
	public boolean isError()
	{
		return false;
	}

}
