package mirrg.lithium.cgi;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

import mirrg.lithium.struct.Struct2;

public class HTTPResponse extends Throwable
{

	public boolean isError;
	public int code;
	public byte[] bytes;
	public int start;
	public int length;

	public HTTPResponse(boolean isError, int code, byte[] bytes, int start, int length)
	{
		this.isError = isError;
		this.code = code;
		this.bytes = bytes;
		this.start = start;
		this.length = length;
	}

	public static HTTPResponse get(int code)
	{
		return get(code, "" + code);
	}

	public static HTTPResponse get(int code, String text)
	{
		return get(code, text.getBytes());
	}

	public static HTTPResponse get(int code, byte[] bytes)
	{
		return get(code, bytes, 0, bytes.length);
	}

	public static HTTPResponse get(int code, Struct2<byte[], Integer> buffer)
	{
		return get(code, buffer.x, 0, buffer.y);
	}

	public static HTTPResponse get(int code, byte[] bytes, int start, int length)
	{
		return new HTTPResponse(code >= 400 && code < 600, code, bytes, start, length);
	}

	public void sendResponse(HttpExchange httpExchange) throws IOException
	{
		httpExchange.sendResponseHeaders(code, length);
		try (OutputStream out = httpExchange.getResponseBody()) {
			out.write(bytes, start, length);
		}
	}

}
