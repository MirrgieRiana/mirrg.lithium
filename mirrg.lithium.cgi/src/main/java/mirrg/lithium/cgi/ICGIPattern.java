package mirrg.lithium.cgi;

import java.io.File;

public interface ICGIPattern
{

	public boolean isCGIScript(File file);

	public HTTPResponse onCGI(String path, CGIServerSetting cgiServerSetting, File file, String pathInfo);

}
