package mirrg.lithium.logging;

import static org.junit.Assert.*;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;

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
		LoggerPrintWriter logger = new LoggerPrintWriter(out2);
		logger.fatal("001");
		logger.error("002");
		logger.warn("003");
		logger.info("004");
		logger.debug("005");
		logger.trace("006");
		assertTrue(out.toString().matches(""
			+ ".{23} \\[FATAL] 001" + System.lineSeparator()
			+ ".{23} \\[ERROR] 002" + System.lineSeparator()
			+ ".{23} \\[WARN]  003" + System.lineSeparator()
			+ ".{23} \\[INFO]  004" + System.lineSeparator()
			+ ".{23} \\[DEBUG] 005" + System.lineSeparator()
			+ ".{23} \\[TRACE] 006" + System.lineSeparator()));
	}

	@Test
	public void test_LoggerTextPane() throws Exception
	{
		JFrame frame = new JFrame();
		frame.setLayout(new CardLayout());
		LoggerTextPane logger = new LoggerTextPane(8);
		logger.fatal("001");
		logger.fatal("001");
		logger.fatal("001");
		logger.fatal("001");
		logger.fatal("001");
		logger.error("002");
		logger.warn("003");
		logger.info("004");
		logger.debug("005");
		logger.trace("006");
		JScrollPane scrollPane = new JScrollPane(logger);
		scrollPane.setPreferredSize(new Dimension(300, 200));
		frame.add(scrollPane);
		Thread.sleep(1000);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		assertTrue(logger.getText().matches(""
			+ ".{23} \\[FATAL] 001" + System.lineSeparator()
			+ ".{23} \\[FATAL] 001" + System.lineSeparator()
			+ ".{23} \\[FATAL] 001" + System.lineSeparator()
			+ ".{23} \\[ERROR] 002" + System.lineSeparator()
			+ ".{23} \\[WARN]  003" + System.lineSeparator()
			+ ".{23} \\[INFO]  004" + System.lineSeparator()
			+ ".{23} \\[DEBUG] 005" + System.lineSeparator()
			+ ".{23} \\[TRACE] 006" + System.lineSeparator()));
		Thread.sleep(1000);
		frame.dispose();
	}

}
