package mirrg.lithium.logging;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * Swingコンポーネントとして出力するロガーです。
 * 表示可能な最大行数を決めることができます。
 * 大量のログが出力される可能性がある場合は、別途ファイル出力などと併用してください。
 */
public class LoggerTextPane extends Logger
{

	public final Style STYLE_FATAL;
	public final Style STYLE_ERROR;
	public final Style STYLE_WARN;
	public final Style STYLE_INFO;
	public final Style STYLE_DEBUG;
	public final Style STYLE_TRACE;

	private int maxLines;

	private JTextPane textPane;
	private DefaultStyledDocument document;

	public LoggerTextPane(int maxLines)
	{
		this.maxLines = maxLines;

		document = new DefaultStyledDocument();
		textPane = new JTextPane(document);
		textPane.setEditable(false);
		textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, textPane.getFont().getSize()));

		STYLE_FATAL = document.addStyle("fatal", document.getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(STYLE_FATAL, Color.white);
		StyleConstants.setBackground(STYLE_FATAL, Color.red);
		STYLE_ERROR = document.addStyle("error", document.getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(STYLE_ERROR, Color.red);
		STYLE_WARN = document.addStyle("warn", document.getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(STYLE_WARN, Color.decode("#ff8800"));
		STYLE_INFO = document.addStyle("info", document.getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(STYLE_INFO, Color.black);
		STYLE_DEBUG = document.addStyle("debug", document.getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(STYLE_DEBUG, Color.decode("#44aaaa"));
		STYLE_TRACE = document.addStyle("trace", document.getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(STYLE_TRACE, Color.decode("#aaaaaa"));
	}

	public JTextPane getTextPane()
	{
		return textPane;
	}

	//

	@Override
	public void println(String string, Optional<EnumLogLevel> oLogLevel)
	{
		Style style = null;
		if (oLogLevel.isPresent()) {
			switch (oLogLevel.get()) {
				case FATAL:
					style = STYLE_FATAL;
					break;
				case ERROR:
					style = STYLE_ERROR;
					break;
				case WARN:
					style = STYLE_WARN;
					break;
				case INFO:
					style = STYLE_INFO;
					break;
				case DEBUG:
					style = STYLE_DEBUG;
					break;
				case TRACE:
					style = STYLE_TRACE;
					break;
			}
		}

		printlnDirectly(LoggingUtil.INSTANCE.format(string, oLogLevel), style);
	}

	/**
	 * このメソッドはどのスレッドからでも呼び出すことができます。
	 */
	public void printlnDirectly(String string, Color foreColor)
	{
		printlnDirectly(string, a -> {
			StyleConstants.setForeground(a, foreColor);
		});
	}

	/**
	 * このメソッドはどのスレッドからでも呼び出すことができます。
	 */
	public void printlnDirectly(String string, Color foreColor, Color backColor)
	{
		printlnDirectly(string, a -> {
			StyleConstants.setForeground(a, foreColor);
			StyleConstants.setBackground(a, backColor);
		});
	}

	/**
	 * このメソッドはどのスレッドからでも呼び出すことができます。
	 */
	public void printlnDirectly(String string, Consumer<SimpleAttributeSet> styleSetter)
	{
		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		styleSetter.accept(attributeSet);
		printlnDirectly(string, attributeSet);
	}

	/**
	 * このメソッドはどのスレッドからでも呼び出すことができます。
	 */
	public void printlnDirectly(String string)
	{
		printlnDirectly(string, (AttributeSet) null);
	}

	/**
	 * このメソッドはどのスレッドからでも呼び出すことができます。
	 */
	public void printlnDirectly(String string, AttributeSet attributeSet)
	{
		for (String string2 : string.split("\\r\\n|\\r|\\n")) {
			printlnDirectlyImpl(string2, attributeSet);
		}
	}

	//

	private ArrayDeque<Integer> lineLengths = new ArrayDeque<>();

	private void printlnDirectlyImpl(String line, AttributeSet attributeSet)
	{
		SwingUtilities.invokeLater(() -> {
			try {
				lineLengths.addLast(line.length());
				document.insertString(document.getLength(), line + "\n", attributeSet);

				while (lineLengths.size() > maxLines) {
					int length = lineLengths.removeFirst();
					document.remove(0, length + 1);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		});
	}

}
