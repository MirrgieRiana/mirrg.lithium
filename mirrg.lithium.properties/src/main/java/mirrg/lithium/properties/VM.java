package mirrg.lithium.properties;

import java.util.function.Consumer;

import mirrg.lithium.parser.core.ResultOxygen;

public class VM
{

	public Properties properties;
	public PropertiesSource propertiesSource;
	public Consumer<Exception> onException;
	public String line;
	public int row;
	public ResultOxygen<Consumer<VM>> result;

	public VM(Properties properties, PropertiesSource propertiesSource, Consumer<Exception> onException, String line, int row, ResultOxygen<Consumer<VM>> result)
	{
		this.properties = properties;
		this.propertiesSource = propertiesSource;
		this.onException = onException;
		this.line = line;
		this.row = row;
		this.result = result;
	}

}
