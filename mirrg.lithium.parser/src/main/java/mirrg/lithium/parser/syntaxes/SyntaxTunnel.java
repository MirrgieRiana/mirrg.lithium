package mirrg.lithium.parser.syntaxes;

import static mirrg.lithium.parser.HSyntaxOxygen.*;

import java.util.function.Supplier;

import mirrg.lithium.parser.core.Memo;
import mirrg.lithium.parser.core.Node;
import mirrg.lithium.parser.core.Syntax;

public class SyntaxTunnel<T> extends Syntax<Supplier<T>>
{

	public final SyntaxExtract<Supplier<T>> syntaxExtract = new SyntaxExtract<>();

	@Override
	protected Node<Supplier<T>> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index)
	{
		return syntaxExtract.parse(memo, shouldTokenProposal, text, index);
	}

	public SyntaxTunnel<T> and(Syntax<?> syntax)
	{
		syntaxExtract.and(syntax);
		return this;
	}

	public SyntaxTunnel<T> extract(Syntax<T> syntax)
	{
		syntaxExtract.extract(pack(syntax, t -> () -> t));
		return this;
	}

}
