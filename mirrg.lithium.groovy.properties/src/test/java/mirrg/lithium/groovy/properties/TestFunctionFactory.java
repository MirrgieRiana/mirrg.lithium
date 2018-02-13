package mirrg.lithium.groovy.properties;

import java.io.IOException;

import groovy.lang.Binding;

public class TestFunctionFactory extends GroovyProperties
{

	public static TestFunction eval(String resourceName, int arg) throws Exception
	{
		return (TestFunction) new GroovyProperties() {
			@Override
			protected void bindVariables(Binding binding)
			{
				binding.setVariable("arg", arg);
			}

			@Override
			protected void registerProtocols(ResourceResolver resourceResolver)
			{
				resourceResolver.registerProtocol("assets", new PathResolverClass(TestFunctionFactory.class));
			}

			@Override
			protected String convertScript(String script) throws IOException
			{
				return resourceResolver.getResourceAsString("assets://header.groovy") + System.lineSeparator() + script;
			}
		}.eval(resourceName);
	}

}
