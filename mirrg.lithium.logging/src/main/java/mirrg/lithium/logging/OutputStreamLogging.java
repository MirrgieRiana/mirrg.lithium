package mirrg.lithium.logging;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class OutputStreamLogging extends OutputStream
{

	private Logger logger;
	private CharsetDecoder charsetDecoder;

	private ByteBuffer in;
	private CharBuffer out;

	private StringBuilder stringBuilder = new StringBuilder();
	private boolean afterR = false;

	public OutputStreamLogging(Logger logger)
	{
		this(logger, Charset.defaultCharset());
	}

	public OutputStreamLogging(Logger logger, String charset)
	{
		this(logger, Charset.forName(charset));
	}

	public OutputStreamLogging(Logger logger, Charset charset)
	{
		this.logger = logger;
		this.charsetDecoder = charset.newDecoder();

		in = ByteBuffer.wrap(new byte[2000]);
		in.limit(0); // 入力データを空に

		out = CharBuffer.wrap(new char[1000]);
	}

	private final byte[] byte1 = new byte[1];

	@Override
	public void write(int b)
	{
		byte1[0] = (byte) b;
		write(byte1, 0, 1);
	}

	@Override
	public void write(byte[] b, int off, int len)
	{
		// この時点で
		// inは必ず0~limitまでが未読データになっている
		// outは必ず未処理のデータがない

		while (true) {
			int empty = in.capacity() - in.limit(); // 空き容量

			if (empty >= len) {
				// すべてのデータを受け付けられる

				// データを挿入
				in.position(in.limit());
				in.limit(in.limit() + len);
				in.put(b, off, len);

				decode();

				break;
			} else {
				// emptyまでしか受け付けられない

				// データを挿入
				in.position(in.limit());
				in.limit(in.limit() + empty);
				in.put(b, off, empty);

				decode();

				len -= empty;
				off += empty;
			}

		}

	}

	@Override
	public void close()
	{
		line();
	}

	private void decode()
	{
		// この時点で
		// inは必ず0~limitが未読データになっている
		// outは必ず未処理のデータがない

		in.position(0);
		out.position(0);
		out.limit(out.capacity());

		// デコード
		charsetDecoder.decode(in, out, false);
		// in.positionが読み取れた分だけ移動
		// out.positionが書き込まれた分だけ移動

		int len = out.position(); // 読み取れた文字数
		accept(out.array(), 0, len);

		// 未処理入力文字
		int remaining = in.limit() - in.position();
		System.arraycopy(in.array(), in.position(), in.array(), 0, remaining);
		in.limit(remaining);

	}

	private void accept(char[] string, int off, int len)
	{
		for (int i = 0; i < len; i++) {
			if (string[i + off] == '\r') {

				stringBuilder.append(string, off, i);
				off += i;
				len -= i;
				i -= i;
				line();

				off += 1;
				len -= 1;
				i -= 1;
			}
			if (string[i + off] == '\n') {

				if (!afterR) {
					stringBuilder.append(string, off, i);
					off += i;
					len -= i;
					i -= i;
					line();
				}

				off += 1;
				len -= 1;
				i -= 1;
			}
			afterR = string[i + off] == '\r';
		}

		stringBuilder.append(string, off, len);
	}

	private void line()
	{
		logger.println(stringBuilder.toString());
		stringBuilder.setLength(0);
	}

}
