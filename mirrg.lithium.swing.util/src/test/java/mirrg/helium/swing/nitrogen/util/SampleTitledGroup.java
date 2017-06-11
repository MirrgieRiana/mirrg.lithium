package mirrg.helium.swing.nitrogen.util;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JList;

public class SampleTitledGroup
{

	public static void main(String[] args)
	{
		new Frame() {

			@Override
			protected void init()
			{
				setLayout(new BorderLayout());
				{
					TitledGroup titledGroup = new TitledGroup("Title");
					titledGroup.setLayout(new CardLayout());
					titledGroup.add(createScrollPane(new JList<String>(IntStream.range(0, 20)
						.mapToObj(i -> "" + i)
						.collect(Collectors.toCollection(Vector::new))), 200, 200));
					add(titledGroup);
				}
			}

		}.setVisible(true);
	}

}
