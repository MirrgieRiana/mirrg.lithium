package mirrg.lithium.cgi.routing;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import mirrg.lithium.cgi.HTTPResponse;
import mirrg.lithium.cgi.HTTPResponseBytes;
import mirrg.lithium.cgi.routing.CGIRouter.EnumRouteResult;
import mirrg.lithium.struct.Tuple;

public class HttpHandlerCGIRouting implements HttpHandler
{

	private CGIRouter[] cgiRouters;

	public HttpHandlerCGIRouting(CGIRouter[] cgiRouters)
	{
		this.cgiRouters = cgiRouters;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException
	{
		new Thread(() -> {
			try {
				for (CGIRouter cgiRouter : cgiRouters) {
					Tuple<EnumRouteResult, HTTPResponse> result = cgiRouter.route(httpExchange.getRequestURI().getPath());
					if (result.x.found) throw result.y;
				}
				throw get404(httpExchange);
			} catch (HTTPResponse httpResponse) {
				try {
					httpResponse.sendResponse(httpExchange);
					return;
				} catch (IOException e1) {
					try {
						get500(httpExchange, e1).sendResponse(httpExchange);
						return;
					} catch (IOException e2) {
						onException(httpExchange, e1, e2);
					}
				}
			}
		}).start();
	}

	protected HTTPResponseBytes get404(HttpExchange httpExchange)
	{
		return HTTPResponse.get(404);
	}

	protected HTTPResponseBytes get500(HttpExchange httpExchange, IOException e)
	{
		return HTTPResponse.get(500);
	}

	protected void onException(HttpExchange httpExchange, IOException e1, IOException e2)
	{
		e2.addSuppressed(e1);
		e2.printStackTrace();
	}

}
