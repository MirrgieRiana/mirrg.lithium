package mirrg.lithium.properties;

import java.util.Optional;

public class PropertyBasic implements IProperty
{

	private Optional<String> oString;

	public PropertyBasic(Optional<String> oString)
	{
		this.oString = oString;
	}

	public PropertyBasic(String string)
	{
		this(Optional.of(string));
	}

	public PropertyBasic()
	{
		this(Optional.empty());
	}

	@Override
	public Optional<String> getString()
	{
		return oString;
	}

	@Override
	public String toString()
	{
		return getString().orElse("");
	}

}
