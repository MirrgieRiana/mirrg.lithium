package mirrg.helium.swing.nitrogen.util;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionListener;

public abstract class Frame extends JFrame
{

	public Frame()
	{
		init();

		pack();
		setLocationByPlatform(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	protected abstract void init();

	protected static JScrollPane createScrollPane(Component component, int width, int height)
	{
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setPreferredSize(new Dimension(width, height));
		return scrollPane;
	}

	protected static <T> JList<T> addListSelectionListener(JList<T> list, ListSelectionListener listener)
	{
		list.addListSelectionListener(listener);
		return list;
	}

}
