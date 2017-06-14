package mirrg.lithium.parser.syntaxes;

import java.util.ArrayList;

import mirrg.lithium.parser.core.Memo;
import mirrg.lithium.parser.core.Node;
import mirrg.lithium.parser.core.Syntax;

public class SyntaxRepeat<T> extends Syntax<ArrayList<T>>
{

	public final Syntax<T> syntax;
	public final int min;
	public final int max;

	public SyntaxRepeat(Syntax<T> syntax, int min, int max)
	{
		this.syntax = syntax;
		this.min = min;
		this.max = max;
	}

	@Override
	protected Node<ArrayList<T>> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index)
	{
		ArrayList<Node<?>> children = new ArrayList<>();
		ArrayList<T> value = new ArrayList<>();
		int begin = index;
		int end = begin;

		while (true) {
			Node<T> node = syntax.parse(memo, shouldTokenProposal, text, index);
			if (node == null) break;
			children.add(node);
			value.add(node.value);
			index += node.end - node.begin;
			end = node.end;
		}

		return new Node<>(this, children, begin, end, value);
	}

}
