package mirrg.lithium.groovy.properties;

import java.io.File;
import java.io.IOException;

import groovy.lang.Binding;

public class TestFunctionFactory extends GroovyProperties
{

	public static final ResourceResolver RESOURCE_RESOLVER;
	static {
		RESOURCE_RESOLVER = new ResourceResolver(new PathResolverFileSystem(new File(".")));
		RESOURCE_RESOLVER.setPathResolver("assets", new PathResolverClass(TestFunctionFactory.class));
	}

	public static TestFunction createTestFunction(String resourceName, String charset, int arg) throws Exception
	{
		return (TestFunction) new TestFunctionFactory(RESOURCE_RESOLVER, arg).eval(resourceName, charset);
	}

	private int arg;

	public TestFunctionFactory(ResourceResolver resourceResolver, int arg)
	{
		super(resourceResolver);
		this.arg = arg;
	}

	@Override
	protected void bindVariables(Binding binding)
	{
		binding.setVariable("arg", arg);
	}

	@Override
	protected String convertScript(String script) throws IOException
	{
		return URLUtil.getString(getResourceResolver().getResource("assets://header.groovy"), "UTF-8") + System.lineSeparator() + script;
	}

}
