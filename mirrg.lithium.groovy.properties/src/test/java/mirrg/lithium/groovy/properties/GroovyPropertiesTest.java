package mirrg.lithium.groovy.properties;

import java.io.File;
import java.io.IOException;

import groovy.lang.Binding;

public class GroovyPropertiesTest extends GroovyProperties
{

	public static TestFunction eval(String resourceName, int arg) throws Exception
	{
		return new GroovyPropertiesTest(arg).eval(resourceName);
	}

	private int arg;

	private ResourceResolver resourceResolver;

	public GroovyPropertiesTest(int arg)
	{
		this.arg = arg;

		resourceResolver = new ResourceResolver(new PathResolverFileSystem(new File(".")));
		resourceResolver.registerProtocol("assets", new PathResolverClass(GroovyPropertiesTest.class));
	}

	public TestFunction eval(String resourceName) throws Exception
	{
		return eval(resourceResolver.getResourceAsURL(resourceName), TestFunction.class);
	}

	@Override
	protected void bindVariables(Binding binding)
	{
		binding.setVariable("arg", arg);
	}

	@Override
	protected void registerProtocols(ResourceResolver resourceResolver)
	{
		resourceResolver.registerProtocol("assets", new PathResolverClass(GroovyPropertiesTest.class));
	}

	@Override
	protected String convertScript(String script) throws IOException
	{
		return resourceResolver.getResourceAsString("assets://header.groovy") + System.lineSeparator() + script;
	}

}
