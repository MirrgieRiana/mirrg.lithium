package mirrg.lithium.cgi;

import java.io.File;

public class CGIServerSetting
{

	public final int port;
	public final String software;
	public final File documentRoot;
	public final int timeoutMs;
	public final CGIBufferPool cgiBufferPool;

	public CGIServerSetting(
		int port,
		String software,
		File documentRoot,
		int timeoutMs,
		CGIBufferPool cgiBufferPool)
	{
		this.port = port;
		this.software = software;
		this.documentRoot = documentRoot;
		this.timeoutMs = timeoutMs;
		this.cgiBufferPool = cgiBufferPool;
	}

}
