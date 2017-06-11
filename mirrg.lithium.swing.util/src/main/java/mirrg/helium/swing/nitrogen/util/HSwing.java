package mirrg.helium.swing.nitrogen.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class HSwing
{

	public static void setWindowsLookAndFeel()
	{
		try {
			UIManager
				.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public static boolean tryAddWebLookAndFeel()
	{
		try {
			Class<?> clazz = Class.forName("com.alee.laf.WebLookAndFeel");
			if (clazz != null) {

				Field[] fields = clazz.getFields();

				Pattern pattern = Pattern.compile("global[\\w\\d_]*Font");
				Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

				for (Field field : fields) {
					if (pattern.matcher(field.getName()).matches()) {
						if (field.getType().isInstance(font)) {
							try {
								field.set(null, font);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					}
				}

				UIManager.installLookAndFeel(new LookAndFeelInfo(clazz
					.getSimpleName(), clazz.getName()));

			}

			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static Component createSplitPaneHorizontal(Component... components)
	{
		return createSplitPaneHorizontal(Arrays.asList(components));
	}

	public static Component createSplitPaneHorizontal(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
			components.get(0),
			createSplitPaneHorizontal(components.subList(1, components.size())));
	}

	public static Component createSplitPaneVertical(Component... components)
	{
		return createSplitPaneVertical(Arrays.asList(components));
	}

	public static Component createSplitPaneVertical(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
			components.get(0),
			createSplitPaneVertical(components.subList(1, components.size())));
	}

	public static Component createBorderPanelUp(Component... components)
	{
		return createBorderPanelUp(Arrays.asList(components));
	}

	public static Component createBorderPanelUp(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		panel.add(components.get(0), BorderLayout.NORTH);
		{
			Component component = createBorderPanelUp(components.subList(1, components.size()));
			if (component != null) panel.add(component, BorderLayout.CENTER);
		}

		return panel;
	}

	public static Component createBorderPanelDown(Component... components)
	{
		return createBorderPanelDown(Arrays.asList(components));
	}

	public static Component createBorderPanelDown(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		{
			Component component = createBorderPanelDown(components.subList(0, components.size() - 1));
			if (component != null) panel.add(component, BorderLayout.CENTER);
		}
		panel.add(components.get(components.size() - 1), BorderLayout.SOUTH);

		return panel;
	}

	public static Component createBorderPanelLeft(Component... components)
	{
		return createBorderPanelLeft(Arrays.asList(components));
	}

	public static Component createBorderPanelLeft(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		panel.add(components.get(0), BorderLayout.WEST);
		{
			Component component = createBorderPanelLeft(components.subList(1, components.size()));
			if (component != null) panel.add(component, BorderLayout.CENTER);
		}

		return panel;
	}

	public static Component createBorderPanelRight(Component... components)
	{
		return createBorderPanelRight(Arrays.asList(components));
	}

	public static Component createBorderPanelRight(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		{
			Component component = createBorderPanelRight(components.subList(0, components.size() - 1));
			if (component != null) panel.add(component, BorderLayout.CENTER);
		}
		panel.add(components.get(components.size() - 1), BorderLayout.EAST);

		return panel;
	}

	public static JPanel createBorderPanelVertical(Component top, Component middle, Component bottom)
	{
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		if (top != null) panel.add(top, BorderLayout.NORTH);
		if (middle != null) panel.add(middle, BorderLayout.CENTER);
		if (bottom != null) panel.add(bottom, BorderLayout.SOUTH);

		return panel;
	}

	public static JPanel createBorderPanelHorizontal(Component left, Component center, Component right)
	{
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		if (left != null) panel.add(left, BorderLayout.WEST);
		if (center != null) panel.add(center, BorderLayout.CENTER);
		if (right != null) panel.add(right, BorderLayout.EAST);

		return panel;
	}

	public static JPanel createPanel(Consumer<JPanel> initializer)
	{
		JPanel panel = new JPanel();
		initializer.accept(panel);
		return panel;
	}

	public static JPanel createPanel(Component... components)
	{
		JPanel panel = new JPanel();
		Stream.of(components)
			.forEach(panel::add);
		return panel;
	}

	public static JPanel createPanel(List<Component> components)
	{
		JPanel panel = new JPanel();
		components.forEach(panel::add);
		return panel;
	}

	public static JButton createButton(String caption, ActionListener listener)
	{
		JButton button = new JButton(caption);
		button.addActionListener(listener);
		return button;
	}

	public static JScrollPane createScrollPane(Component component)
	{
		JScrollPane scrollPane = new JScrollPane(component);
		return scrollPane;
	}

	public static JScrollPane createScrollPane(Component component, int width, int height)
	{
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setPreferredSize(new Dimension(width, height));
		return scrollPane;
	}

	public static <T> T process(T object, Consumer<T> consumer)
	{
		consumer.accept(object);
		return object;
	}

	public static <T> T get(Supplier<T> supplier)
	{
		return supplier.get();
	}

	public static <T extends Component> T setPreferredSize(T component, int width, int rows)
	{
		component.setPreferredSize(new Dimension(width, component.getFont().getSize() * rows + 6));
		return component;
	}

	public static <T extends AbstractButton> T addActionListener(T component, ActionListener listener)
	{
		component.addActionListener(listener);
		return component;
	}

	public static <T extends Component> T hookRightClick(T component, Predicate<MouseEvent> listener)
	{
		component.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e)
			{

			}

			@Override
			public void mousePressed(MouseEvent e)
			{

			}

			@Override
			public void mouseExited(MouseEvent e)
			{

			}

			@Override
			public void mouseEntered(MouseEvent e)
			{

			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (listener.test(e)) e.consume();
				}
			}

		});
		return component;
	}

	public static <T extends Component> T hookPopup(T component, Predicate<MouseEvent> listener)
	{
		component.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger()) {
					if (listener.test(e)) e.consume();
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger()) {
					if (listener.test(e)) e.consume();
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{

			}

			@Override
			public void mouseEntered(MouseEvent e)
			{

			}

			@Override
			public void mouseClicked(MouseEvent e)
			{

			}

		});
		return component;
	}

	public static <T extends JTextComponent> T hookChange(T textComponent, Consumer<DocumentEvent> listener)
	{
		textComponent.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				listener.accept(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				listener.accept(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				listener.accept(e);
			}

		});
		return textComponent;
	}

	public static <T extends Component> T hookDoubleClick(T component, Consumer<MouseEvent> listener)
	{
		component.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2) {
					listener.accept(e);
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{

			}

			@Override
			public void mouseReleased(MouseEvent e)
			{

			}

			@Override
			public void mouseEntered(MouseEvent e)
			{

			}

			@Override
			public void mouseExited(MouseEvent e)
			{

			}

		});
		return component;
	}

}
