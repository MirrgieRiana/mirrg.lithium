package mirrg.lithium.cgi.routing;

import java.io.File;

import mirrg.lithium.cgi.CGIServerSetting;
import mirrg.lithium.cgi.HTTPResponse;

public interface ICGIPattern
{

	public boolean isCGIScript(File file);

	public HTTPResponse onCGI(String path, CGIServerSetting cgiServerSetting, File file, String pathInfo);

}
