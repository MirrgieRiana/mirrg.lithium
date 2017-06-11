package mirrg.helium.swing.nitrogen.util;

import static mirrg.helium.swing.nitrogen.util.GroupBuilder.*;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class SampleGroupBuilder
{

	public static void main(String[] args)
	{
		new Frame() {

			@Override
			protected void init()
			{
				GroupLayout layout = new GroupLayout(getContentPane());

				group(
					group(
						new JLabel("Label"),
						new JTextField("Text")),
					group(
						new JLabel("Label"),
						new JTextField("Text")),
					group(
						new JLabel("Label"),
						new JTextField("Text")),
					group(
						new JLabel("Label"),
						new JTextField("Text")),
					group(
						new JLabel("Label"),
						new JTextField("Text")),
					group(
						new JLabel("Label"),
						new JTextField("Text")),
					new JScrollPane(new JButton("Button"))).apply(layout);

				setLayout(layout);
			}

		}.setVisible(true);
	}

}
