package mirrg.applications.service.pwi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockCommand extends BlockLineBufferBase
{

	private LineSource source;
	private Launcher launcher;
	private ILineReceiver preSession;
	private ILineReceiver serviceOut;

	public BlockCommand(Logger logger, LineBuffer in, LineSource source, Launcher launcher, ILineReceiver preSession, ILineReceiver serviceOut) throws Exception
	{
		super(logger, in);
		this.source = source;
		this.launcher = launcher;
		this.preSession = preSession;
		this.serviceOut = serviceOut;
	}

	private static Pattern PATTERN_GET = Pattern.compile("/get (.+)");

	@Override
	public void onLine(Line line) throws Exception
	{
		serviceOut.onLine(line);

		if (line.text.startsWith("/")) {
			if (line.text.equals("/set restart false")) {
				launcher.restartable = false;
				serviceOut.onLine(new Line(source, "Changed"));
			} else if (line.text.equals("/set restart true")) {
				launcher.restartable = true;
				serviceOut.onLine(new Line(source, "Changed"));
			} else {
				Matcher matcher = PATTERN_GET.matcher(line.text);
				if (matcher.matches()) {
					serviceOut.onLine(new Line(source, launcher.properties.getString(matcher.group(1)).orElse("undefined")));
				} else if (line.text.equals("/exit")) {
					launcher.restartable = false;
					serviceOut.onLine(new Line(source, "Stopping"));
					launcher.oSession.ifPresent(s -> s.process.destroy());
				} else if (line.text.equals("/stop")) {
					serviceOut.onLine(new Line(source, "Stopping"));
					launcher.oSession.ifPresent(s -> s.process.destroy());
				} else if (line.text.equals("/help")) {
					serviceOut.onLine(new Line(source, "/set restart true　　　プロセスの再起動を許可します。"));
					serviceOut.onLine(new Line(source, "/set restart false　　　プロセスの再起動を不許可にします。"));
					serviceOut.onLine(new Line(source, "/get [property name]　　　プロパティを表示します。"));
					serviceOut.onLine(new Line(source, "/exit　　　現在のプロセスを終了し、サービスを終了します。"));
					serviceOut.onLine(new Line(source, "/stop　　　現在のプロセスを終了し、必要であれば再起動します。"));
					serviceOut.onLine(new Line(source, "/help　　　このメッセージを表示します。"));
				} else if (line.text.startsWith("//")) {
					preSession.onLine(new Line(line.source, line.text.substring(1), line.time));
				} else {
					serviceOut.onLine(new Line(source, "Unknown Command: " + line.text));
				}
			}
		} else {
			preSession.onLine(line);
		}
	}

}
