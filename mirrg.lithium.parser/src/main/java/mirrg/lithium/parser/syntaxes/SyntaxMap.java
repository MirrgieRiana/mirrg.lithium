package mirrg.lithium.parser.syntaxes;

import java.util.function.Function;

import mirrg.lithium.parser.core.Memo;
import mirrg.lithium.parser.core.Node;
import mirrg.lithium.parser.core.Syntax;

public class SyntaxMap<I, O> extends Syntax<O>
{

	public final Syntax<I> syntax;
	public final Function<Node<I>, O> function;

	public SyntaxMap(Syntax<I> syntax, Function<Node<I>, O> function)
	{
		this.syntax = syntax;
		this.function = function;
	}

	@Override
	protected Node<O> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index)
	{
		Node<I> node = syntax.parse(memo, shouldTokenProposal, text, index);
		if (node == null) return null;
		return new Node<>(this, node.children, node.begin, node.end, function.apply(node));
	}

}
