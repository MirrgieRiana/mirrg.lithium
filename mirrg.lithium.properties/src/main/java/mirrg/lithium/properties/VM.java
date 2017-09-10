package mirrg.lithium.properties;

import java.util.function.Consumer;

import mirrg.lithium.parser.core.ResultOxygen;

public class VM
{

	public PropertiesMultipleInheritable properties;
	public PropertiesSource propertiesSource;
	public Consumer<Exception> onException;
	public String source;
	public ResultOxygen<Consumer<VM>> result;
	public String prefix = "";

	public VM(PropertiesMultipleInheritable properties, PropertiesSource propertiesSource, Consumer<Exception> onException, String source, ResultOxygen<Consumer<VM>> result)
	{
		this.properties = properties;
		this.propertiesSource = propertiesSource;
		this.onException = onException;
		this.source = source;
		this.result = result;
	}

	public void putProperty(String key, IMethod method)
	{
		properties.put(prefix + key, method);
	}

}
