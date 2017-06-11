package mirrg.helium.swing.nitrogen.util;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public class TitledGroup extends JComponent
{

	private static final long serialVersionUID = -2129682085019459410L;

	public TitledGroup(String title)
	{
		if (title != null) setBorder(BorderFactory.createTitledBorder(title));
	}

}
