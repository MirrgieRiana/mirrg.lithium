package mirrg.lithium.parser.syntaxes;

import mirrg.lithium.parser.core.Memo;
import mirrg.lithium.parser.core.Node;
import mirrg.lithium.parser.core.Syntax;

public class SyntaxString extends Syntax<String>
{

	public final String string;

	public SyntaxString(String string)
	{
		this.string = string;
	}

	@Override
	protected Node<String> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index)
	{
		if (index + string.length() > text.length()) return null;
		for (int i = 0; i < string.length(); i++) {
			if (text.charAt(index + i) != string.charAt(i)) return null;
		}
		return new Node<>(this, null, index, index + string.length(), string);
	}

	@Override
	public String getName()
	{
		String name = super.getName();
		if (name != null) return name;
		return string;
	}

}
