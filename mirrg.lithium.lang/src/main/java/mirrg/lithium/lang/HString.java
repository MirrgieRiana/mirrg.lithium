package mirrg.lithium.lang;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HString
{

	static final Pattern PATTERN_NUMBER = Pattern.compile("\\A[0-9]+");
	static final Pattern PATTERN_NOT_NUMBER = Pattern.compile("\\A[^0-9]+");

	public static int compareWithNumber(String a, String b)
	{
		return compareWithNumberImpl(a, b, String::compareTo);
	}

	public static int compareWithNumberIgnoreCase(String a, String b)
	{
		return compareWithNumberImpl(a, b, String::compareToIgnoreCase);
	}

	public static int compareWithNumberImpl(String a, String b, Comparator<String> comparator)
	{
		SplitStringStream sssa = new SplitStringStream(a);
		SplitStringStream sssb = new SplitStringStream(b);

		while (true) {
			boolean inNumber = sssa.isNextStringInNumber();

			String sa = sssa.getNext();
			String sb = sssb.getNext();

			if (sa == null) {
				if (sb != null) {
					return -1;
				} else {
					return 0;
				}
			} else {
				if (sb == null) {
					return 1;
				} else {
					if (inNumber) {
						int la = sa.length();
						int lb = sb.length();

						int length = Math.max(la, lb);

						sa = rept('0', length - la) + sa;
						sb = rept('0', length - lb) + sb;
					}

					int compare = comparator.compare(sa, sb);

					if (compare != 0)
						return compare > 0 ? 1 : -1;
				}
			}
		}

	}

	public static String rept(char ch, int t)
	{
		StringBuffer sb = new StringBuffer(t);
		for (int i = 0; i < t; i++) {
			sb.append(ch);
		}
		return sb.toString();
	}

	public static String rept(String string, int t)
	{
		StringBuffer sb = new StringBuffer(string.length() + t);
		for (int i = 0; i < t; i++) {
			sb.append(string);
		}
		return sb.toString();
	}

	public static class SplitStringStream
	{

		private String string;
		private boolean inNumber;

		public SplitStringStream(String string)
		{
			this.string = string;
			inNumber = false;
		}

		public String getNext()
		{
			if (string.isEmpty())
				return null;
			Pattern pattern = inNumber ? PATTERN_NUMBER : PATTERN_NOT_NUMBER;
			inNumber = !inNumber;
			Matcher m = pattern.matcher(string);
			if (!m.find())
				return "";
			string = string.substring(m.end());
			return m.group();
		}

		public boolean isNextStringInNumber()
		{
			return inNumber;
		}

	}

	/**
	 * 小数点以下12桁の文字列表現を作成し、末尾に来た0と.を除去した物を返します。
	 */
	public static String toString(double d)
	{
		StringBuffer sb = new StringBuffer(String.format("%.12f", d));
		while (sb.charAt(sb.length() - 1) == '0') {
			sb.setLength(sb.length() - 1);
		}
		if (sb.charAt(sb.length() - 1) == '.') {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 10進数の整数に解析します。
	 *
	 * @see Integer#parseInt(String, int)
	 * @return null: 解析できなかったとき<br>
	 *         文字列表現を整数にしたもの
	 */
	public static Integer parseInt10(String string)
	{
		try {
			return Integer.parseInt(string, 10);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static char toUpperCase(char ch)
	{
		if ('a' <= ch && ch <= 'z')
			ch += 'A' - 'a';
		return ch;
	}

	public static char toLowerCase(char ch)
	{
		if ('A' <= ch && ch <= 'Z')
			ch += 'a' - 'A';
		return ch;
	}

	private static Pattern lineBreak = Pattern.compile("(?:\n|\r\n?)");

	public static LineProvider getLineProvider(String text)
	{
		LineProvider lineProvider = new LineProvider();
		Matcher matcher = lineBreak.matcher(text);

		lineProvider.text = text;
		lineProvider.lines = 1;

		//
		lineProvider.lineNumberToCharacterIndex.put(1, 0);
		lineProvider.entries.add(new int[] {
			1, 0,
		});
		//

		int indexPreviousStartIndex = 0;
		while (matcher.find()) {

			int indexStartMatch = matcher.start();
			int lengthLineBreak = matcher.group(0).length();

			//
			lineProvider.lineNumberToCharacterIndex.put(lineProvider.lines + 1, indexStartMatch + lengthLineBreak);
			lineProvider.entries.add(new int[] {
				lineProvider.lines + 1, indexStartMatch + lengthLineBreak,
			});
			lineProvider.contentLengthes.add(indexStartMatch - indexPreviousStartIndex);
			lineProvider.lineLengthes.add(indexStartMatch + lengthLineBreak - indexPreviousStartIndex);
			indexPreviousStartIndex = indexStartMatch + lengthLineBreak;
			//

			lineProvider.lines++;
		}

		//
		lineProvider.contentLengthes.add(text.length() - indexPreviousStartIndex);
		lineProvider.lineLengthes.add(text.length() - indexPreviousStartIndex);
		//

		return lineProvider;
	}

	/**
	 * 行番号と行頭の文字番号の対応。行番号は1から始まり、文字番号は0から始まる。
	 */
	public static class LineProvider
	{

		protected String text;
		protected Hashtable<Integer, Integer> lineNumberToCharacterIndex = new Hashtable<>();
		protected ArrayList<int[]> entries = new ArrayList<>();
		protected ArrayList<Integer> contentLengthes = new ArrayList<>();
		protected ArrayList<Integer> lineLengthes = new ArrayList<>();
		protected int lines;

		public String getText()
		{
			return text;
		}

		public int getLineCount()
		{
			return lines;
		}

		/**
		 * 文字列長以上の値を入れた場合、最後の行扱いになる。
		 */
		public int getLineNumber(int characterIndex)
		{
			if (characterIndex < 0) {
				throw new IllegalArgumentException("characterIndex must be >= 0: " + characterIndex);
			}
			if (characterIndex >= text.length()) {
				return entries.get(entries.size() - 1)[0];
			}

			int now = 0;
			for (int[] entry : entries) {
				if (characterIndex >= entry[1]) {
					now = entry[0];
				} else {
					return now;
				}
			}

			return now;
		}

		public Set<Entry<Integer, Integer>> entrySet()
		{
			return lineNumberToCharacterIndex.entrySet();
		}

		public int getStartIndex(int lineNumber)
		{
			return lineNumberToCharacterIndex.get(lineNumber);
		}

		public int getContentLength(int lineNumber)
		{
			return contentLengthes.get(lineNumber - 1);
		}

		public String getContent(int lineNumber)
		{
			return text.substring(getStartIndex(lineNumber), getStartIndex(lineNumber) + getContentLength(lineNumber));
		}

		public int getLineLength(int lineNumber)
		{
			return lineLengthes.get(lineNumber - 1);
		}

		public String getLine(int lineNumber)
		{
			return text.substring(getStartIndex(lineNumber), getStartIndex(lineNumber) + getLineLength(lineNumber));
		}

	}

	/**
	 * 小数点以下n桁まで表示する文字列を返す。
	 *
	 * @param effectiveDigit
	 *            n
	 */
	public static String getEffectiveExpression(double value, int effectiveDigit)
	{
		if (effectiveDigit < 0) {
			return String.format("%.0f", value);
		} else {
			return String.format("%." + effectiveDigit + "f", value);
		}
	}

}
