package mirrg.lithium.cgi;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class HTTPResponseBytes extends HTTPResponse
{

	public boolean isError;
	public int code;
	public byte[] bytes;
	public int start;
	public int length;

	public HTTPResponseBytes(boolean isError, int code, byte[] bytes, int start, int length)
	{
		this.isError = isError;
		this.code = code;
		this.bytes = bytes;
		this.start = start;
		this.length = length;
	}

	@Override
	public void sendResponse(HttpExchange httpExchange) throws IOException
	{
		send(httpExchange, code, bytes, start, length);
	}

	@Override
	public boolean isError()
	{
		return isError;
	}

}
