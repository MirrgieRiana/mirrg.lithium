package mirrg.lithium.cgi;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import mirrg.lithium.struct.Tuple;

public class CGIRouter
{

	public CGIServerSetting cgiServerSetting;

	private ArrayList<String> indexes = new ArrayList<>();
	private ArrayList<ICGIPattern> cgiPatterns = new ArrayList<>();

	public CGIRouter(CGIServerSetting cgiServerSetting)
	{
		this.cgiServerSetting = cgiServerSetting;
	}

	public void addIndex(String index)
	{
		indexes.add(index);
	}

	public void addCGIPattern(ICGIPattern cgiPattern)
	{
		cgiPatterns.add(cgiPattern);
	}

	/**
	 * <p>
	 * 引数のパスを"/"で区切り左側から順番に試していく。
	 * </p>
	 * <p>
	 * 残りのパスが空の場合、定義済みのindexファイル名を試す（index置換とpath_infoは共存できない）。
	 * どのindexも無かった場合、404を返す。
	 * </p>
	 * <p>
	 * 残りのパスが"/"を含まない場合、それを階層名として試す。
	 * ファイルかCGIだったらそれを処理し、ディレクトリだったらインデックスを表示する。
	 * そうでなければ404を返す。
	 * </p>
	 * <p>
	 * 残りのパスが"/"を含む場合、そこまでを階層名として一旦区切る。
	 * 階層名が"" "." ".."だった場合、404を返す。
	 * CGIがあったらそれを実行し、ディレクトリだったら次の階層に進む。
	 * ファイルが見つかったかディレクトリがなかった場合、404を返す。
	 * </p>
	 */
	public Tuple<EnumRouteResult, HTTPResponse> route(String path) // path = "/a/b/c.cgi/d"
	{
		String pathRight = path;

		if (!pathRight.startsWith("/")) return new Tuple<>(EnumRouteResult.ILLEGAL_FORMAT, onError(path));
		pathRight = pathRight.substring(1); // path = "a/b/c.cgi/d"

		String pathLeft = "";
		// path2: 常に先頭と末尾が"/"が除去済み
		// path: 常に先頭が"/"が除去済み

		while (true) {

			if (pathRight.isEmpty()) {
				// path = ""
				for (String index : indexes) {
					File file = new File(cgiServerSetting.documentRoot, pathLeft + "/" + index);
					Optional<ICGIPattern> oCgi = isCGI(file);
					if (oCgi.isPresent()) {
						return new Tuple<>(EnumRouteResult.CGI, oCgi.get().onCGI(path, cgiServerSetting, file, ""));
					} else if (isFile(file)) {
						return new Tuple<>(EnumRouteResult.FILE, onFile(path, file));
					}
				}
				return new Tuple<>(EnumRouteResult.NO_INDEX, onError(path));
			}

			int i = pathRight.indexOf('/');
			if (i == -1) {
				// path = "d"
				File file = new File(cgiServerSetting.documentRoot, pathLeft + "/" + pathRight);
				Optional<ICGIPattern> oCgi = isCGI(file);
				if (oCgi.isPresent()) {
					return new Tuple<>(EnumRouteResult.CGI, oCgi.get().onCGI(path, cgiServerSetting, file, ""));
				} else if (isFile(file)) {
					return new Tuple<>(EnumRouteResult.FILE, onFile(path, file));
				} else if (isDirectory(file)) {
					return new Tuple<>(EnumRouteResult.DIRECTORY, onDirectory(path, file));
				}
				return new Tuple<>(EnumRouteResult.NO_FILE, onError(path));
			}

			String dir = pathRight.substring(0, i);
			if (dir.isEmpty()) return new Tuple<>(EnumRouteResult.ILLEGAL_FORMAT, onError(path));
			if (dir.equals(".")) return new Tuple<>(EnumRouteResult.ILLEGAL_FORMAT, onError(path));
			if (dir.equals("..")) return new Tuple<>(EnumRouteResult.ILLEGAL_FORMAT, onError(path));

			if (!pathLeft.isEmpty()) pathLeft += "/";
			pathLeft += dir; // path2 = "a" "a/b" "a/b/c.cgi"
			pathRight = pathRight.substring(i + 1); // path = "a/b/c.cgi/d" "b/c.cgi/d" "c.cgi/d"

			File file = new File(cgiServerSetting.documentRoot, pathLeft);
			Optional<ICGIPattern> oCgi = isCGI(file);
			if (oCgi.isPresent()) {
				return new Tuple<>(EnumRouteResult.CGI, oCgi.get().onCGI(path, cgiServerSetting, file, "/" + pathRight));
			} else if (isFile(file)) {
				return new Tuple<>(EnumRouteResult.NO_FILE, onError(path));
			}

			if (!isDirectory(file)) {
				return new Tuple<>(EnumRouteResult.NO_FILE, onError(path));
			}

		}
	}

	public static enum EnumRouteResult
	{
		ILLEGAL_FORMAT(false),
		NO_INDEX(false),
		NO_FILE(false),
		FILE(true),
		CGI(true),
		DIRECTORY(true),
		;

		public final boolean found;

		private EnumRouteResult(boolean found)
		{
			this.found = found;
		}

	}

	public boolean isFile(File file)
	{
		return file.isFile();
	}

	public Optional<ICGIPattern> isCGI(File file)
	{
		if (!file.exists()) return Optional.empty();
		for (ICGIPattern cgiPattern : cgiPatterns) {
			if (cgiPattern.isCGIScript(file)) {
				return Optional.of(cgiPattern);
			}
		}
		return Optional.empty();
	}

	public boolean isDirectory(File file)
	{
		return file.isDirectory();
	}

	protected HTTPResponse onFile(String path, File file)
	{
		return HTTPResponse.get(file);
	}

	protected HTTPResponse onDirectory(String path, File file)
	{
		return HTTPResponse.get(404, "404: " + path);
	}

	protected HTTPResponse onError(String path)
	{
		return HTTPResponse.get(404, "404: " + path);
	}

	/*
	try

	{
		String path = e.getRequestURI().getPath();
		String username = e.getPrincipal() == null ? "Guest" : e.getPrincipal().getUsername();

		// 異常URLの除去
		if ((path + "/").indexOf("/../") != -1) {
			send(e, 403, "403");
			return;
		}

		// index
		if (path.endsWith("/")) {

			for (String dir : setting.homeDirectory) {
				for (String index : setting.indexes) {
					File file = new File(dir, path.substring(1) + index);
					if (file.isFile()) {
						redirect(e, path + index);
						return;
					}
				}
			}

			send(e, 404, "404");
			return;
		}

		// CGI/ファイル転送
		for (String dir : setting.homeDirectory) {
			File file = new File(dir, path.substring(1));
			if (file.isFile()) {

				for (CGISetting cgiSetting : setting.cgiSettings) {
					if (file.getPath().endsWith(cgiSetting.fileNameSuffix)) {
						new CGIRunner(
							new CGIServerSetting(
								setting.http.port,
								getServerName() + "/" + getServerVersion(),
								new File(dir),
								setting.http.timeoutMs,
								cgiBufferPool),
							e,
							cgiSetting.commandFormat,
							file,
							Optional.empty(), // TODO
							Optional.empty(), // TODO
							new ILogger() {
								@Override
								public void accept(Exception e)
								{
									logger.log(e);
								}

								@Override
								public void accept(String message)
								{
									logger.log(message);
								}
							},
							1000).run();
						return;
					}
				}

				sendFile(e, file.toURI().toURL());
				return;
			}
		}

		send(e, 404, "404");
		return;
	}catch(
	IOException e1)
	{
		try {
			send(e, 500, "500");
		} catch (IOException e2) {
			e2.addSuppressed(e1);
			e2.printStackTrace();
		}
		return;
	}
	*/

}
