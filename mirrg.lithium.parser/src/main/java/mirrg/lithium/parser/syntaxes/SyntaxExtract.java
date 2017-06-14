package mirrg.lithium.parser.syntaxes;

import static mirrg.lithium.parser.HSyntaxOxygen.*;

import mirrg.lithium.parser.core.Memo;
import mirrg.lithium.parser.core.Node;
import mirrg.lithium.parser.core.Syntax;
import mirrg.lithium.struct.Struct1;

public class SyntaxExtract<T> extends Syntax<T>
{

	public final SyntaxSerial<Struct1<T>> syntaxSerial = serial(Struct1<T>::new);
	public final SyntaxPack<Struct1<T>, T> syntaxPack = pack(syntaxSerial, Struct1::getX);

	@Override
	protected Node<T> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index)
	{
		return syntaxPack.parse(memo, shouldTokenProposal, text, index);
	}

	public SyntaxExtract<T> and(Syntax<?> syntax)
	{
		syntaxSerial.and(syntax);
		return this;
	}

	public SyntaxExtract<T> extract(Syntax<T> syntax)
	{
		syntaxSerial.and(syntax, Struct1::setX);
		return this;
	}

}
