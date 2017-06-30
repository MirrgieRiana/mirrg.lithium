package mirrg.lithium.properties;

import mirrg.lithium.lang.HString;

public class SyntaxException extends Exception
{

	private VM vm;
	private int column;

	public SyntaxException(VM vm, int column)
	{
		this.vm = vm;
		this.column = column;
	}

	@Override
	public String getMessage()
	{
		return String.format("Syntax error at %s (R:%s C:%s)\n%s\n%s^",
			vm.propertiesSource.sourceName,
			vm.row,
			column,
			vm.line,
			HString.rept(" ", column - 1));
	}

}
