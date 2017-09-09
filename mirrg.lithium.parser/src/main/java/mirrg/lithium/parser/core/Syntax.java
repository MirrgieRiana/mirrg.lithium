package mirrg.lithium.parser.core;

public abstract class Syntax<T>
{

	public String name;

	public Node<T> parse(Memo memo, boolean shouldTokenProposal, String text, int index)
	{
		if (!shouldTokenProposal) return memo.get(this, index, () -> parseImpl(memo, false, text, index));
		if (getName() == null) return memo.get(this, index, () -> parseImpl(memo, true, text, index));
		memo.addTokenProposal(index, this);
		return memo.get(this, index, () -> parseImpl(memo, false, text, index));
	}

	protected abstract Node<T> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index);

	public Node<T> parse(String text)
	{
		ResultOxygen<T> result = matches(text);
		if (result.isValid) {
			return result.node;
		} else {
			return null;
		}
	}

	public ResultOxygen<T> matches(String text)
	{
		return matches(text, 0, text.length());
	}

	public ResultOxygen<T> matches(String text, int begin)
	{
		return matches(text, begin, text.length());
	}

	public ResultOxygen<T> matches(String text, int begin, int end)
	{
		Memo memo = new Memo();
		Node<T> node = parse(memo, true, text, begin);
		if (node == null) return new ResultOxygen<>(null, memo, false);
		return new ResultOxygen<>(node, memo, node.end == end);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

}
