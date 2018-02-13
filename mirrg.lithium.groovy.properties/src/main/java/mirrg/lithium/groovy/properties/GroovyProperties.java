package mirrg.lithium.groovy.properties;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * Groovyプロパティファイルの呼び出しごとに生成するクラスです。
 */
public class GroovyProperties
{

	protected ResourceResolver resourceResolver;

	public GroovyProperties()
	{
		resourceResolver = new ResourceResolver(new PathResolverFileSystem(new File(".")));
		registerProtocols(resourceResolver);
	}

	public Object eval(String resourceName) throws Exception
	{
		return eval(resourceResolver.getResourceAsURL(resourceName));
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
		return new GroovyShell(createBinding(scriptURL, baseURL)).evaluate(convertScript(script));
	}

	protected Binding createBinding(URL scriptURL, URL baseURL) throws IOException
	{
		Binding binding = new Binding();
		bindVariables(binding);
		binding.setVariable("context", createContext(scriptURL, baseURL));
		return binding;
	}

	protected ResourceResolverContext createContext(URL scriptURL, URL baseURL) throws IOException
	{
		ResourceResolverContext resourceResolver = new ResourceResolverContext(new PathResolverURL(baseURL), scriptURL, baseURL);
		registerProtocols(resourceResolver);
		return resourceResolver;
	}

	protected void bindVariables(Binding binding)
	{

	}

	protected void registerProtocols(ResourceResolver resourceResolver)
	{

	}

	protected String convertScript(String script) throws IOException
	{
		return script;
	}

	public class ResourceResolverContext extends ResourceResolver
	{

		private URL scriptURL;
		private URL baseURL;

		private ResourceResolverContext(IPathResolver pathResolverDefault, URL scriptURL, URL baseURL)
		{
			super(pathResolverDefault);
			this.scriptURL = scriptURL;
			this.baseURL = baseURL;
		}

		public Object eval(String resourceName) throws Exception
		{
			return GroovyProperties.this.eval(getResourceAsURL(resourceName));
		}

		public URL getScriptURL()
		{
			return scriptURL;
		}

		public URL getBaseURL()
		{
			return baseURL;
		}

	}

}
