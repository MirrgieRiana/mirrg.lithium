package mirrg.lithium.properties;

public class IllegalConstantAccessSyntaxException extends SyntaxException
{

	public IllegalConstantAccessSyntaxException(VM vm, int index)
	{
		super(vm, index);
	}

}
