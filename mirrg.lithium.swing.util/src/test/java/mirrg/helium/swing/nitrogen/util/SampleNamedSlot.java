package mirrg.helium.swing.nitrogen.util;

import java.awt.BorderLayout;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

public class SampleNamedSlot
{

	private static JTextField textField;
	private static JList<NamedSlot<SampleObject>> list1;
	private static JList<NamedSlot<SampleObject>> list2;

	public static void main(String[] args)
	{
		new Frame() {

			@Override
			protected void init()
			{
				setLayout(new BorderLayout());
				{

					SampleObject[] objects = IntStream.range(0, 20)
						.mapToObj(i -> new SampleObject(i, "[" + i + "]"))
						.toArray(SampleObject[]::new);

					add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
						createScrollPane(addListSelectionListener(list1 = new JList<NamedSlot<SampleObject>>(Stream.of(objects)
							.map(o -> new NamedSlot<>(o, SampleObject::getName))
							.collect(Collectors.toCollection(Vector::new))), e -> {
								textField.setText("" + list1.getSelectedValue().get().value);
							}), 100, 200),
						createScrollPane(addListSelectionListener(list2 = new JList<NamedSlot<SampleObject>>(Stream.of(objects)
							.map(o -> new NamedSlot<>(o, SampleObject::getName))
							.collect(Collectors.toCollection(Vector::new))), e -> {
								textField.setText("" + list2.getSelectedValue().get().value);
							}), 100, 200)), BorderLayout.CENTER);

					add(textField = new JTextField(), BorderLayout.SOUTH);
				}
			}

		}.setVisible(true);
	}

	public static class SampleObject
	{

		public int value;
		public String name;

		public SampleObject(int value, String name)
		{
			this.value = value;
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

	}

}
