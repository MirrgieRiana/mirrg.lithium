package mirrg.lithium.groovy.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GroovyPropertiesContext
{

	private GroovyProperties groovyProperties;
	private URL scriptURL;
	private URL baseURL;

	private ResourceResolver resourceResolver;

	/**
	 * @param groovyProperties
	 * @param scriptURL
	 *            null: スクリプトがURLに紐付けされていない場合
	 * @param baseURL
	 */
	public GroovyPropertiesContext(GroovyProperties groovyProperties, URL scriptURL, URL baseURL)
	{
		this.groovyProperties = groovyProperties;
		this.scriptURL = scriptURL;
		this.baseURL = baseURL;

		this.resourceResolver = new ResourceResolver(groovyProperties.getResourceResolver());
		this.resourceResolver.setDefaultPathResolver(new PathResolverURL(baseURL));
	}

	public ResourceResolver getResourceResolver()
	{
		return resourceResolver;
	}

	/**
	 * スクリプトのURLを返します。スクリプトがURLに紐付けされていない場合はnullが返されます。
	 */
	public URL getScriptURL()
	{
		return scriptURL;
	}

	/**
	 * このコンテキストにおける相対参照の基底を返します。
	 */
	public URL getBaseURL()
	{
		return baseURL;
	}

	/**
	 * このリソースへの参照をURLで得ます。
	 *
	 * @throws IOException
	 *             参照先のリソースが存在しない場合にも発生する可能性があります。
	 */
	public URL getResourceAsURL(String resourceName) throws IOException
	{
		return resourceResolver.getResource(resourceName);
	}

	/**
	 * このリソースの内容をストリームで得ます。
	 *
	 * @throws IOException
	 *             参照先のリソースが存在しない場合にも発生する可能性があります。
	 */
	public InputStream getResourceAsStream(String resourceName) throws IOException
	{
		return URLUtil.getStream(getResourceAsURL(resourceName));
	}

	/**
	 * このリソースの内容をバイナリで得ます。
	 *
	 * @throws IOException
	 *             参照先のリソースが存在しない場合にも発生する可能性があります。
	 */
	public byte[] getResourceAsBytes(String resourceName) throws IOException
	{
		return URLUtil.getBytes(getResourceAsURL(resourceName));
	}

	/**
	 * このリソースの内容を文字列で得ます。
	 *
	 * @throws IOException
	 *             参照先のリソースが存在しない場合にも発生する可能性があります。
	 */
	public String getResourceAsString(String resourceName, String charset) throws IOException
	{
		return URLUtil.getString(getResourceAsURL(resourceName), charset);
	}

	/**
	 * リソース名で表されるGroovyスクリプトを実行し、戻り値を得ます。
	 */
	public Object eval(String resourceName, String charset) throws Exception
	{
		return groovyProperties.eval(getResourceAsURL(resourceName), charset);
	}

}
