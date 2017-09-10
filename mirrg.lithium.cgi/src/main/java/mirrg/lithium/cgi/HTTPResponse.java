package mirrg.lithium.cgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;

import mirrg.lithium.struct.Tuple;

public abstract class HTTPResponse extends Throwable
{

	public abstract void sendResponse(HttpExchange httpExchange) throws IOException;

	public abstract boolean isError();

	public static HTTPResponseSendFile get(File file)
	{
		return new HTTPResponseSendFile(file);
	}

	public static HTTPResponseBytes get(int code)
	{
		return get(code, "" + code);
	}

	public static HTTPResponseBytes get(int code, String text)
	{
		return get(code, text.getBytes());
	}

	public static HTTPResponseBytes get(int code, byte[] bytes)
	{
		return get(code, bytes, 0, bytes.length);
	}

	public static HTTPResponseBytes get(int code, byte[] bytes, int start, int length)
	{
		return get(code >= 400 && code < 600, code, bytes, start, length);
	}

	public static HTTPResponseBytes get(boolean isError, int code, byte[] bytes, int start, int length)
	{
		return new HTTPResponseBytes(isError, code, bytes, start, length);
	}

	//

	public static void send(HttpExchange httpExchange, int code, String string) throws IOException
	{
		send(httpExchange, code, string.getBytes());
	}

	public static void send(HttpExchange httpExchange, int code, String string, String charset) throws IOException
	{
		send(httpExchange, code, string.getBytes(charset));
	}

	public static void send(HttpExchange httpExchange, int code, byte[] bytes) throws IOException
	{
		send(httpExchange, code, bytes, 0, bytes.length);
	}

	public static void send(HttpExchange httpExchange, int code, byte[] bytes, int start, int length) throws IOException
	{
		httpExchange.sendResponseHeaders(code, length);
		try (OutputStream out = httpExchange.getResponseBody()) {
			out.write(bytes, start, length);
		}
	}

	public static void send(HttpExchange httpExchange, int code) throws IOException
	{
		httpExchange.sendResponseHeaders(code, 0);
		try (OutputStream out = httpExchange.getResponseBody()) {

		}
	}

	public static void redirect(HttpExchange httpExchange, String url) throws IOException
	{
		httpExchange.getResponseHeaders().add("Location", url);
		send(httpExchange, 301);
	}

	public static void setContentType(HttpExchange httpExchange, String contentType)
	{
		httpExchange.getResponseHeaders().add("Content-Type", contentType);
	}

	public static void setContentType(HttpExchange httpExchange, String contentType, String charset)
	{
		httpExchange.getResponseHeaders().add("Content-Type", contentType + "; charset=" + charset);
	}

	public static void sendFile(HttpExchange httpExchange, URL url) throws IOException
	{
		try (InputStream in = url.openStream()) {
			sendFile(httpExchange, in);
		}
	}

	public static void sendFile(HttpExchange httpExchange, InputStream in) throws IOException
	{
		ArrayList<Tuple<byte[], Integer>> buffers = new ArrayList<>();
		byte[] bytes = new byte[4000];
		while (true) {
			int len = in.read(bytes);
			if (len == -1) break;
			buffers.add(new Tuple<>(bytes, len));
		}

		httpExchange.sendResponseHeaders(200, buffers.stream()
			.mapToInt(t -> t.y)
			.sum());
		try (OutputStream out = httpExchange.getResponseBody()) {
			for (Tuple<byte[], Integer> buffer : buffers) {
				out.write(buffer.x, 0, buffer.y);
			}
		}
	}

	public static void sendFile(HttpExchange httpExchange, File file) throws IOException
	{
		try (InputStream in = new FileInputStream(file)) {
			sendFile(httpExchange, in, file.length());
		}
	}

	public static void sendFile(HttpExchange httpExchange, InputStream in, long length) throws IOException
	{
		httpExchange.sendResponseHeaders(200, length);
		try (OutputStream out = httpExchange.getResponseBody()) {
			byte[] bytes = new byte[4000];
			while (true) {
				int len = in.read(bytes);
				if (len == -1) break;
				out.write(bytes, 0, len);
			}
		}
	}

}
