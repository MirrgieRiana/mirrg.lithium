package mirrg.helium.swing.nitrogen.util;

import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;

public class GroupBuilder
{

	public static GroupBuilder group(Object... objects)
	{
		return new GroupBuilder(objects);
	}

	private Object[] objects;
	private Alignment alignment = null;

	public GroupBuilder(Object[] objects)
	{
		this.objects = objects;
	}

	public Group build(GroupLayout groupLayout)
	{
		return build(groupLayout, true);
	}

	public Group build(GroupLayout groupLayout, boolean isSequential)
	{
		Group group = isSequential
			? groupLayout.createSequentialGroup()
			: alignment != null
				? groupLayout.createParallelGroup(alignment)
				: groupLayout.createParallelGroup();

		for (Object object : objects) {
			if (object instanceof GroupBuilder) {
				group.addGroup(((GroupBuilder) object).build(groupLayout,
					!isSequential));
			} else if (object instanceof Component) {
				group.addComponent((Component) object);
			} else {
				throw new RuntimeException("unknown group build entry: "
					+ object);
			}
		}

		return group;
	}

	public void apply(GroupLayout layout)
	{
		layout.setHorizontalGroup(build(layout, false));
		layout.setVerticalGroup(build(layout));
	}

	public GroupBuilder align(Alignment alignment)
	{
		this.alignment = alignment;
		return this;
	}

}
