package mirrg.lithium.properties;

import mirrg.lithium.lang.HString;
import mirrg.lithium.parser.core.ResultOxygen;

public class SyntaxException extends Exception
{

	private String sourceName;
	private String line;
	private int lineNumber;
	private ResultOxygen<?> result;

	public SyntaxException(String sourceName, String line, int lineNumber, ResultOxygen<?> result)
	{
		this.sourceName = sourceName;
		this.line = line;
		this.lineNumber = lineNumber;
		this.result = result;
	}

	@Override
	public String getMessage()
	{
		return String.format("Syntax error at %s (R:%s C:%s)\n%s\n%s^",
			sourceName,
			lineNumber,
			result.getTokenProposalIndex() + 1,
			line,
			HString.rept(" ", result.getTokenProposalIndex()));
	}

}
