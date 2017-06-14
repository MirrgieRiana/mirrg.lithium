package mirrg.lithium.parser.core;

import java.util.List;

public class Node<T>
{

	public final Syntax<?> syntax;
	public final List<Node<?>> children;
	public final int begin;
	/**
	 * このインデックスを含まない。
	 */
	public final int end;
	public final T value;

	public Node(Syntax<?> syntax, List<Node<?>> children, int begin, int end, T value)
	{
		this.syntax = syntax;
		this.children = children;
		this.begin = begin;
		this.end = end;
		this.value = value;

		if (value instanceof IListenerNode) {
			((IListenerNode) value).setNode(this);
		}
	}

}
