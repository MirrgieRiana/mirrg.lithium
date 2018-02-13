package mirrg.lithium.groovy.properties;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * リソース名をリソースを表すURLに変換します。<br>
 * リソース名を表す文字列は次のどれかでなければなりません。
 * <ul>
 * <li>プロトコル指定のない絶対もしくは相対パス名（例："waveform.csv"）</li>
 * <li>URLの文字列表現（例："file:/c:/waveform.csv"）</li>
 * <li>アプリケーションで定義されたリソース（例："assets://sample.groovy"）</li>
 * </ul>
 */
public class ResourceResolver
{

	private static final Pattern PATTERN_PROTOCOL = Pattern.compile("\\A([^:]*):/");

	private Hashtable<String, IPathResolver> pathResolvers = new Hashtable<>();

	public ResourceResolver()
	{
		this.pathResolvers = new Hashtable<>();
	}

	public ResourceResolver(IPathResolver pathResolverDefault)
	{
		this.pathResolvers = new Hashtable<>();
		this.pathResolvers.put("", pathResolverDefault);
	}

	public ResourceResolver(ResourceResolver other)
	{
		this.pathResolvers = new Hashtable<>(other.pathResolvers);
	}

	public IPathResolver getDefaultPathResolver()
	{
		return pathResolvers.get("");
	}

	public void setDefaultPathResolver(IPathResolver pathResolver)
	{
		setPathResolver("", pathResolver);
	}

	public Hashtable<String, IPathResolver> getPathResolvers()
	{
		return pathResolvers;
	}

	public IPathResolver getPathResolver(String protocol)
	{
		return pathResolvers.get(protocol);
	}

	public void setPathResolver(String protocol, IPathResolver pathResolver)
	{
		pathResolvers.put(protocol, pathResolver);
	}

	/**
	 * @throws IOException
	 *             参照先のリソースが存在しない場合にも発生する可能性があります。
	 */
	public URL getResource(String resourceName) throws IOException
	{
		Matcher matcher = PATTERN_PROTOCOL.matcher(resourceName);
		if (matcher.find()) {
			// プロトコル指定あり
			String protocol = matcher.group(1);
			IPathResolver pathResolver = pathResolvers.get(protocol);
			if (pathResolver != null) {
				// 独自プロトコル
				return pathResolver.getResource(resourceName.substring(protocol.length() + 3)); // "protocol" + "://"
			} else {
				// デフォルトプロトコル
				return new URL(resourceName);
			}
		} else {
			// 単純パス
			IPathResolver pathResolver = pathResolvers.get("");
			if (pathResolver == null) throw new IOException("Default path is not supported: " + resourceName);
			return pathResolver.getResource(resourceName);
		}
	}

}
