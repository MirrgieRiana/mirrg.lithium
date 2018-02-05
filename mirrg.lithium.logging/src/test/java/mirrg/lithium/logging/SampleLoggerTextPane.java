package mirrg.lithium.logging;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

public class SampleLoggerTextPane
{

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();

		frame.setLayout(new BorderLayout());
		LoggerTextPane loggerTextPane = new LoggerTextPane(50);
		JScrollPane scrollPane = new JScrollPane(loggerTextPane.getTextPane());
		scrollPane.setPreferredSize(new Dimension(300, 200));
		frame.add(scrollPane);

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);

		for (int i = 0; i < 100; i++) {
			loggerTextPane.info("" + i);
		}
	}

}
