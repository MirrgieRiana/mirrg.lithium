package mirrg.lithium.properties;

import java.util.Optional;

public interface IProperties
{

	public Optional<IMethod> getMethod(String key);

	public default IProperty get(String key)
	{
		return getMethod(key).map(m -> m.apply(this)).orElse(new PropertyBasic());
	}

}
