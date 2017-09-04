package mirrg.lithium.cgi;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class HTTPResponseImpl extends HTTPResponse
{

	public boolean isError;
	private ISendResponse sendResponse;

	public HTTPResponseImpl(boolean isError, ISendResponse sendResponse)
	{
		this.isError = isError;
		this.sendResponse = sendResponse;
	}

	@Override
	public void sendResponse(HttpExchange httpExchange) throws IOException
	{
		sendResponse.sendResponse(httpExchange);
	}

	@Override
	public boolean isError()
	{
		return isError;
	}

	public static interface ISendResponse
	{

		public void sendResponse(HttpExchange httpExchange) throws IOException;

	}

}
