package mirrg.lithium.lang;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.Consumer;

public class HLog
{

	public static ArrayList<String> log = new ArrayList<>();
	public static ArrayList<Consumer<String>> listeners = new ArrayList<>();
	public static PrintStream out;

	static {
		try {
			out = new PrintStream(new OutputStream() {

				// private ArrayList<Byte> line = new ArrayList<>();

				private byte[] bytes = new byte[100];
				private int length = 0;

				private boolean beforeR = false;

				@Override
				public synchronized void write(int b) throws IOException
				{
					if (b == '\r') {
						beforeR = true;

						log(new String(bytes, 0, length));

						length = 0;
					} else if (b == '\n') {
						if (beforeR) {
							beforeR = false;
						} else {

							log(new String(bytes, 0, length));

							length = 0;
						}
					} else {
						add((byte) b);
					}
				}

				private void add(byte b)
				{
					if (bytes.length == length) {
						byte[] tmp = new byte[bytes.length * 3 / 2];
						System.arraycopy(bytes, 0, tmp, 0, length);
						bytes = tmp;
					}

					bytes[length] = b;
					length++;
				}

			}, true, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void log(String string)
	{
		String e = "[" + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")) + "] " + string;
		log.add(e);
		listeners.forEach(c -> c.accept(e));
		System.out.println(e);
	}

	public static void log(String format, Object... arguments)
	{
		log(String.format(format, arguments));
	}

}
