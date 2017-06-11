package mirrg.lithium.lang;

import static mirrg.lithium.lang.HString.*;
import static org.junit.Assert.*;

import org.junit.Test;

import mirrg.lithium.lang.HString.LineProvider;

public class TestHString
{

	@Test
	public void test_tableLineNumberToCharacterIndex()
	{
		String text = ""
			+ "\r\n"
			+ "11111\r"
			+ "111\n"
			+ "11111111\r\n"
			+ "11111\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "1111\r"
			+ "\r"
			+ "\r"
			+ "1111\n"
			+ "\n"
			+ "\n";

		LineProvider lineProvider = HString.getLineProvider(text);

		String[] string = {
			""
		};
		lineProvider.entrySet().stream().sequential().sorted((a, b) -> {
			if (a.getKey() < b.getKey()) return -1;
			if (a.getKey() > b.getKey()) return 1;
			return 0;
		}).forEach(entry -> {
			string[0] += "[" + entry.getKey() + ":" + +entry.getValue() + "]";
		});

		assertEquals("[1:0][2:2][3:8][4:12][5:22][6:29][7:31][8:33][9:38][10:39][11:40][12:45][13:46][14:47]",
			string[0]);
	}

	@Test
	public void test_getLineNumber()
	{
		String text = ""
			+ "\r\n" // 1
			+ "11111\r" // 2
			+ "111\n" // 3
			+ "11111111\r\n" // 4
			+ "11111\r\n" // 5
			+ "\r\n" // 6
			+ "\r\n" // 7
			+ "1111\r" // 8
			+ "\r" // 9
			+ "\r" // 10
			+ "1111\n" // 11
			+ "\n" // 12
			+ "\n"; // 13
		// 14

		LineProvider lineProvider = HString.getLineProvider(text);

		assertEquals(14, lineProvider.getLineCount());

		{
			int i = 1;
			int c = 0;
			assertEquals(i, lineProvider.getLineNumber(c++)); // 0: \r
			assertEquals(i, lineProvider.getLineNumber(c++)); // 1: \n
			i++;
			assertEquals(i, lineProvider.getLineNumber(c++)); // 2: 1
			assertEquals(i, lineProvider.getLineNumber(c++)); // 3: 1
			assertEquals(i, lineProvider.getLineNumber(c++)); // 4: 1
			assertEquals(i, lineProvider.getLineNumber(c++)); // 5: 1
			assertEquals(i, lineProvider.getLineNumber(c++)); // 6: 1
			assertEquals(i, lineProvider.getLineNumber(c++)); // 7: \r
			i++;
			assertEquals(i, lineProvider.getLineNumber(c++)); // 8: 1
			assertEquals(i, lineProvider.getLineNumber(c++)); // 9: 1
			assertEquals(i, lineProvider.getLineNumber(c++)); // 10: 1
			assertEquals(i, lineProvider.getLineNumber(c++)); // 11: \r

			// 最後の文字以降は最後の行扱い
			assertEquals(14, lineProvider.getLineNumber(text.length())); // 47: \Z
			assertEquals(14, lineProvider.getLineNumber(text.length() + 10)); // 57: null
		}

		{
			int l = 1;
			assertEquals(0, lineProvider.getContentLength(l++)); // 1
			assertEquals(5, lineProvider.getContentLength(l++)); // 2
			assertEquals(3, lineProvider.getContentLength(l++)); // 3
			assertEquals(8, lineProvider.getContentLength(l++)); // 4
			assertEquals(5, lineProvider.getContentLength(l++)); // 5
			assertEquals(0, lineProvider.getContentLength(l++)); // 6
			assertEquals(0, lineProvider.getContentLength(l++)); // 7
			assertEquals(4, lineProvider.getContentLength(l++)); // 8
			assertEquals(0, lineProvider.getContentLength(l++)); // 9
			assertEquals(0, lineProvider.getContentLength(l++)); // 10
			assertEquals(4, lineProvider.getContentLength(l++)); // 11
			assertEquals(0, lineProvider.getContentLength(l++)); // 12
			assertEquals(0, lineProvider.getContentLength(l++)); // 13
			assertEquals(0, lineProvider.getContentLength(l++)); // 14
		}

		assertEquals("11111", lineProvider.getContent(2));
		assertEquals("", lineProvider.getContent(6));
		assertEquals("", lineProvider.getContent(9));
		assertEquals("", lineProvider.getContent(14));

		{
			int l = 1;
			assertEquals(2, lineProvider.getLineLength(l++));
			assertEquals(6, lineProvider.getLineLength(l++));
			assertEquals(4, lineProvider.getLineLength(l++));
			assertEquals(10, lineProvider.getLineLength(l++));
			assertEquals(7, lineProvider.getLineLength(l++));
			assertEquals(2, lineProvider.getLineLength(l++));
			assertEquals(2, lineProvider.getLineLength(l++));
			assertEquals(5, lineProvider.getLineLength(l++));
			assertEquals(1, lineProvider.getLineLength(l++));
			assertEquals(1, lineProvider.getLineLength(l++));
			assertEquals(5, lineProvider.getLineLength(l++));
			assertEquals(1, lineProvider.getLineLength(l++));
			assertEquals(1, lineProvider.getLineLength(l++));
			assertEquals(0, lineProvider.getLineLength(l++));
		}

		assertEquals("11111\r", lineProvider.getLine(2));
		assertEquals("\r\n", lineProvider.getLine(6));
		assertEquals("\r", lineProvider.getLine(9));
		assertEquals("", lineProvider.getLine(14));

	}

	@Test
	public void test_getEffectiveExpression()
	{
		assertEquals("12", getEffectiveExpression(12.125664897, 0));
		assertEquals("12.1", getEffectiveExpression(12.125664897, 1));
		assertEquals("12.13", getEffectiveExpression(12.125664897, 2));
		assertEquals("12.126", getEffectiveExpression(12.125664897, 3));
		assertEquals("12.1257", getEffectiveExpression(12.125664897, 4));
		assertEquals("12.12566", getEffectiveExpression(12.125664897, 5));
		assertEquals("5", getEffectiveExpression(5, 0));
		assertEquals("5.0", getEffectiveExpression(5, 1));
		assertEquals("5.00", getEffectiveExpression(5, 2));
		assertEquals("5.000", getEffectiveExpression(5, 3));
		assertEquals("5.0000", getEffectiveExpression(5, 4));
		assertEquals("5.00000", getEffectiveExpression(5, 5));
	}

}
