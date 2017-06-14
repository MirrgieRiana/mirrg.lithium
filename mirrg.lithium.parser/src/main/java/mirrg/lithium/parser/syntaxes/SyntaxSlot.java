package mirrg.lithium.parser.syntaxes;

import java.util.function.Supplier;

import mirrg.lithium.parser.core.Memo;
import mirrg.lithium.parser.core.Node;
import mirrg.lithium.parser.core.Syntax;

public class SyntaxSlot<T> extends Syntax<T>
{

	public final Supplier<Syntax<T>> supplier;
	public Syntax<T> syntax;

	public SyntaxSlot()
	{
		this(null);
	}

	public SyntaxSlot(Supplier<Syntax<T>> supplier)
	{
		this.supplier = supplier;
	}

	@Override
	protected Node<T> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index)
	{
		if (syntax == null) setSyntax(supplier.get());
		return syntax.parse(memo, shouldTokenProposal, text, index);
	}

	public void setSyntax(Syntax<T> syntax)
	{
		this.syntax = syntax;
	}

}
