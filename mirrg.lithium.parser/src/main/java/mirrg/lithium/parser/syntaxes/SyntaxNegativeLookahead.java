package mirrg.lithium.parser.syntaxes;

import mirrg.lithium.parser.core.Memo;
import mirrg.lithium.parser.core.Node;
import mirrg.lithium.parser.core.Syntax;

public class SyntaxNegativeLookahead extends Syntax<Void>
{

	public final Syntax<?> syntax;

	public SyntaxNegativeLookahead(Syntax<?> syntax)
	{
		this.syntax = syntax;
	}

	@Override
	protected Node<Void> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index)
	{
		Node<?> node = syntax.parse(memo, shouldTokenProposal, text, index);
		if (node != null) return null;
		return new Node<>(this, null, index, index, null);
	}

}
