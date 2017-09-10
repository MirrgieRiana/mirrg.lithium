package mirrg.lithium.properties;

import java.util.Optional;
import java.util.stream.Collectors;

import mirrg.lithium.lang.HString;
import mirrg.lithium.lang.HString.LineProvider;
import mirrg.lithium.parser.core.ResultOxygen;

public class SyntaxException extends Exception
{

	private VM vm;
	private Optional<ResultOxygen<?>> oResult = Optional.empty();
	private int index;

	public SyntaxException(VM vm, ResultOxygen<?> result)
	{
		this.vm = vm;
		this.oResult = Optional.of(result);
		this.index = result.getTokenProposalIndex();
	}

	public SyntaxException(VM vm, int index)
	{
		this.vm = vm;
		this.index = index;
	}

	@Override
	public String getMessage()
	{
		LineProvider lineProvider = HString.getLineProvider(vm.source);
		int lineNumber = lineProvider.getLineNumber(index);
		int column = index - lineProvider.getStartIndex(lineNumber);

		String string = String.format("Syntax error at %s (R:%s C:%s)\n%s\n%s^",
			vm.propertiesSource.sourceName,
			lineNumber,
			column + 1,
			lineProvider.getContent(lineNumber),
			HString.rept(" ", column));

		if (oResult.isPresent()) {
			string += String.format("\nExpected: %s",
				oResult.get().getTokenProposal().stream()
					.map(s -> s.getName())
					.filter(n -> n != null)
					.distinct()
					.sorted()
					.collect(Collectors.joining(", ")));
		}

		return string;
	}

}
