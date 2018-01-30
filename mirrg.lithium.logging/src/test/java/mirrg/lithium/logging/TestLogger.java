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
		assertEquals(""
			+ "[FATAL] 001" + System.lineSeparator()
			+ "[ERROR] 002" + System.lineSeparator()
			+ "[WARN]  003" + System.lineSeparator()
			+ "[INFO]  004" + System.lineSeparator()
			+ "[DEBUG] 005" + System.lineSeparator()
			+ "[TRACE] 006" + System.lineSeparator(), out.toString());
	}

	@Test
	public void test_LoggerTetPane() throws Exception
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
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		assertEquals(""
			+ "[FATAL] 001" + System.lineSeparator()
			+ "[FATAL] 001" + System.lineSeparator()
			+ "[FATAL] 001" + System.lineSeparator()
			+ "[ERROR] 002" + System.lineSeparator()
			+ "[WARN]  003" + System.lineSeparator()
			+ "[INFO]  004" + System.lineSeparator()
			+ "[DEBUG] 005" + System.lineSeparator()
			+ "[TRACE] 006" + System.lineSeparator(), logger.getText());
		Thread.sleep(1000);
	}

}
