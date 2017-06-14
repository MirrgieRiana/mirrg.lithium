package mirrg.lithium.parser.core;

import java.util.ArrayList;

public class ResultOxygen<T>
{

	public final Node<T> node;
	public final Memo memo;
	public final boolean isValid;

	public ResultOxygen(Node<T> node, Memo memo, boolean isValid)
	{
		this.node = node;
		this.memo = memo;
		this.isValid = isValid;
	}

	public ArrayList<Syntax<?>> getTokenProposal()
	{
		return memo.getTokenProposal(getTokenProposalIndex());
	}

	public int getMatchedLength()
	{
		return node == null ? 0 : node.end;
	}

	public int getTokenProposalIndex()
	{
		return memo.maxTokenProposalIndex;
	}

}
