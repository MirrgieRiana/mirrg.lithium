package mirrg.lithium.properties;

public class IllegalConstantAccessSyntaxException extends SyntaxException
{

	public IllegalConstantAccessSyntaxException(VM vm, int column)
	{
		super(vm, column);
	}

}
