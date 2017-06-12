package mirrg.applications.bin2csv;

import static mirrg.lithium.swing.util.HSwing.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mirrg.lithium.lang.HLog;

public class Main
{

	public static void main(String[] args)
	{
		new Main().main();
	}

	private JTextPane textPane;

	@SuppressWarnings("unchecked")
	public void main()
	{
		JFrame frame = new JFrame("Bin2Csv");
		frame.setLayout(new CardLayout());
		frame.add(createScrollPane(get(() -> {
			textPane = new JTextPane();
			textPane.setText("ここにファイルをドロップ\n");
			textPane.setEditable(false);
			textPane.setTransferHandler(new TransferHandler() {

				@Override
				public boolean canImport(TransferSupport support)
				{
					for (DataFlavor dataFlavor : support.getDataFlavors()) {
						if (DataFlavor.javaFileListFlavor.equals(dataFlavor)) {
							return support.getDropAction() == MOVE;
						}
					}
					return false;
				}

				@Override
				public boolean importData(TransferSupport support)
				{
					if (support.isDrop()) {
						if (support.getDropAction() == MOVE) {

							List<File> files;
							try {
								Object data = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
								files = (List<File>) data;
							} catch (UnsupportedFlavorException | IOException e) {
								printStackTrace(e);
								return false;
							}

							start(files);

							return true;
						}
					}
					return false;
				}

			});
			return textPane;
		}), 600, 200));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.pack();
		frame.setVisible(true);
	}

	private void start(List<File> files)
	{
		new Thread(() -> {
			for (File file : files) {
				if (file.isFile()) {
					processFile(file);
				} else {
					println("'" + file + "' is not a file!", Optional.empty(), Optional.of(Color.decode("#ffaa88")));
				}
			}
		}).start();
	}

	private void processFile(File file)
	{
		if (file.getName().endsWith(".csv")) {
			File fileOut = new File(file.getParentFile(), file.getName() + ".dat");

			try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				OutputStream out = new FileOutputStream(fileOut)) {

				String line;
				while ((line = in.readLine()) != null) {
					out.write(Integer.parseInt(line, 10) & 0xff);
				}

			} catch (Exception e) {
				printStackTrace(e);
				return;
			}

			println("" + file, Optional.empty(), Optional.of(Color.decode("#88ff88")));

		} else {
			File fileOut = new File(file.getParentFile(), file.getName() + ".csv");

			try (FileInputStream in = new FileInputStream(file);
				PrintStream out = new PrintStream(new FileOutputStream(fileOut))) {

				byte[] bytes = new byte[1024];
				int res;
				while ((res = in.read(bytes)) != -1) {
					for (int i = 0; i < res; i++) {
						out.println((bytes[i]) & 0xff);
					}
				}

			} catch (Exception e) {
				printStackTrace(e);
				return;
			}

			println("" + file, Optional.empty(), Optional.of(Color.decode("#88ff88")));

		}
	}

	private void println(String text, Optional<Color> colorForeground, Optional<Color> colorBackground)
	{
		SwingUtilities.invokeLater(() -> {
			try {
				SimpleAttributeSet a = new SimpleAttributeSet();
				if (colorForeground.isPresent()) a.addAttribute(StyleConstants.Foreground, colorForeground.get());
				if (colorBackground.isPresent()) a.addAttribute(StyleConstants.Background, colorBackground.get());
				textPane.getDocument().insertString(textPane.getDocument().getLength(), text + "\n", a);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		});
	}

	private void printStackTrace(Exception e)
	{
		println(HLog.getStackTrace(e), Optional.empty(), Optional.of(Color.decode("#ff8888")));
	}

}
