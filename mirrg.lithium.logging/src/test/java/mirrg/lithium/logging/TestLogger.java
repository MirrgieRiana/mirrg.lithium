package mirrg.lithium.logging;

import static org.junit.Assert.*;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.junit.Test;

public class TestLogger
{

	@Test
	public void test_LoggerPrintWriter() throws Exception
	{
		StringWriter out = new StringWriter();
		PrintWriter out2 = new PrintWriter(out);
		LogSinkPrintWriter logSink = new LogSinkPrintWriter(out2);
		TaggedLogger logger = new TaggedLogger("Test", logSink);
		logger.fatal("001");
		logger.error("002");
		logger.warn("003");
		logger.info("004");
		logger.debug("005");
		logger.trace("006");
		assertTrue(out.toString().matches(""
			+ ".{23} \\[FATAL] \\[Test] 001" + System.lineSeparator()
			+ ".{23} \\[ERROR] \\[Test] 002" + System.lineSeparator()
			+ ".{23} \\[WARN]  \\[Test] 003" + System.lineSeparator()
			+ ".{23} \\[INFO]  \\[Test] 004" + System.lineSeparator()
			+ ".{23} \\[DEBUG] \\[Test] 005" + System.lineSeparator()
			+ ".{23} \\[TRACE] \\[Test] 006" + System.lineSeparator()));
	}

	@Test
	public void test_LoggerTextPane() throws Exception
	{
		JFrame frame = new JFrame();
		frame.setLayout(new CardLayout());
		LogSinkTextPane logSink = new LogSinkTextPane(8);
		Logger logger = new Logger(logSink);
		logger.fatal("Test", "001");
		logger.fatal("Test", "001");
		logger.fatal("Test", "001");
		logger.fatal("Test", "001");
		logger.fatal("Test", "001");
		logger.error("Test", "002");
		logger.warn("Test", "003");
		logger.info("Test", "004");
		logger.debug("Test", "005");
		logger.trace("Test", "006");
		JScrollPane scrollPane = new JScrollPane(logSink.getTextPane());
		scrollPane.setPreferredSize(new Dimension(300, 200));
		frame.add(scrollPane);
		Thread.sleep(1000);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		if (!logSink.getTextPane().getText().matches(""
			+ ".{23} \\[FATAL] \\[Test] 001" + System.lineSeparator()
			+ ".{23} \\[FATAL] \\[Test] 001" + System.lineSeparator()
			+ ".{23} \\[FATAL] \\[Test] 001" + System.lineSeparator()
			+ ".{23} \\[ERROR] \\[Test] 002" + System.lineSeparator()
			+ ".{23} \\[WARN]  \\[Test] 003" + System.lineSeparator()
			+ ".{23} \\[INFO]  \\[Test] 004" + System.lineSeparator()
			+ ".{23} \\[DEBUG] \\[Test] 005" + System.lineSeparator()
			+ ".{23} \\[TRACE] \\[Test] 006")) {
			fail();
		}
		Thread.sleep(1000);
		frame.dispose();
	}

	@Test
	public void test_OutputStreamLogging() throws Exception
	{
		ArrayList<String> strings = new ArrayList<>();

		test0(strings, "UTF-8");
		test0(strings, "Shift-JIS");
		test0(strings, "Unicode");

		{
			try (PrintStream out2 = new PrintStream(new OutputStreamLogging("Test", new LogSink() {
				@Override
				public void println(String tag, String string, Optional<EnumLogLevel> oLogLevel)
				{
					strings.add("[" + tag + "] " + string);
				}
			}, "Unicode"), true, "Unicode")) {

				out2.println("abc");
				assertEquals(1, strings.size());
				assertEquals("[Test] abc", strings.get(0));
				strings.clear();

				out2.println("def");
				assertEquals(1, strings.size());
				assertEquals("[Test] def", strings.get(0));
				strings.clear();

				out2.println("ghi");
				assertEquals(1, strings.size());
				assertEquals("[Test] ghi", strings.get(0));
				strings.clear();

			}

			assertEquals(1, strings.size());
			assertEquals("[Test] ", strings.get(0));
			strings.clear();
		}

	}

	private void test0(ArrayList<String> strings, String charset) throws UnsupportedEncodingException
	{
		try (PrintStream out2 = new PrintStream(new OutputStreamLogging("Test", new LogSink() {
			@Override
			public void println(String tag, String string, Optional<EnumLogLevel> oLogLevel)
			{
				strings.add("[" + tag + "] " + string);
			}
		}, charset), true, charset)) {

			out2.print("あいうえお\nかきく\rけ\r\nこ");
			out2.flush();
			assertEquals(3, strings.size());
			assertEquals("[Test] あいうえお", strings.get(0));
			assertEquals("[Test] かきく", strings.get(1));
			assertEquals("[Test] け", strings.get(2));
			strings.clear();

		}

		assertEquals(1, strings.size());
		assertEquals("[Test] こ", strings.get(0));
		strings.clear();
	}

}
