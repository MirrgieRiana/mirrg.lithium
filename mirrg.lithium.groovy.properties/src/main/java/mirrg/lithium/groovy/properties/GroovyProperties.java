package mirrg.lithium.groovy.properties;

import java.io.IOException;
import java.net.URL;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * Groovyプロパティファイルの呼び出しごとに生成するクラスです。
 */
public class GroovyProperties
{

	private ResourceResolver resourceResolver;

	public GroovyProperties(ResourceResolver resourceResolver)
	{
		this.resourceResolver = resourceResolver;
	}

	public ResourceResolver getResourceResolver()
	{
		return resourceResolver;
	}

	public Object eval(String resourceName) throws Exception
	{
		return eval(resourceResolver.getResource(resourceName));
	}

	public Object eval(URL scriptURL) throws Exception
	{
		return eval(URLUtil.getString(scriptURL), scriptURL, scriptURL);
	}

	public Object eval(String script, URL baseURL) throws Exception
	{
		return eval(script, null, baseURL);
	}

	public Object eval(String script, URL scriptURL, URL baseURL) throws Exception
	{
		Binding binding = new Binding();
		bindVariables(binding);
		binding.setVariable("context", new GroovyPropertiesContext(this, scriptURL, baseURL));
		return new GroovyShell(binding).evaluate(convertScript(script));
	}

	protected void bindVariables(Binding binding)
	{

	}

	protected String convertScript(String script) throws IOException
	{
		return script;
	}

}
