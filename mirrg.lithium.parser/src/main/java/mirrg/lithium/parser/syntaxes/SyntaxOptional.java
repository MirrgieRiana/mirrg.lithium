package mirrg.lithium.parser.syntaxes;

import java.util.Optional;

import mirrg.lithium.parser.core.Memo;
import mirrg.lithium.parser.core.Node;
import mirrg.lithium.parser.core.Syntax;

public class SyntaxOptional<T> extends Syntax<Optional<T>>
{

	public final Syntax<T> syntax;

	public SyntaxOptional(Syntax<T> syntax)
	{
		this.syntax = syntax;
	}

	@Override
	protected Node<Optional<T>> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index)
	{
		Node<T> node = syntax.parse(memo, shouldTokenProposal, text, index);
		if (node == null) return new Node<>(this, null, index, index, Optional.empty());
		return new Node<>(this, node.children, node.begin, node.end, Optional.of(node.value));
	}

}
