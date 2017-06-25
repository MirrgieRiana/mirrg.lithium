package mirrg.applications.service.pwi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

import mirrg.applications.service.pw2.LineStorage;
import mirrg.applications.service.pw2.Logger;
import mirrg.applications.service.pwi.core.LineBuffer;
import mirrg.applications.service.pwi.core.LineSource;
import mirrg.applications.service.pwi.dispatchers.LineDispatcherInputStream;
import mirrg.applications.service.pwi.dispatchers.LineDispatcherWebInterface;
import mirrg.applications.service.pwi.n.Launcher.Runner;
import mirrg.applications.service.pwi.receivers.LineReceiverService;
import mirrg.lithium.lang.HString;
import mirrg.lithium.properties.Properties;

public class Launcher
{

	private Properties properties;

	private LineBuffer in;
	private LineBuffer out;
	private LineStorage lineStorage;
	private Logger logger;

	public Optional<Runner> oRunner = Optional.empty();
	public volatile boolean restartable;

	public Launcher(Properties properties)
	{
		this.properties = properties;
		properties.put("time.id", p -> getTimeId());
		properties.put("service.number.id", p -> HString.fillLeft('0', p.get("service.number"), 5));
		properties.put("session.number.id", p -> HString.fillLeft('0', p.get("session.number"), 5));

		in = new LineBuffer();
		out = new LineBuffer();
		lineStorage = new LineStorage(properties.getInteger("log.lines").get());
		logger = new Logger(out, new LineSource("SERVICE", "magenta"));

		restartable = properties.getBoolean("restartable").get();
	}

	public void run() throws Exception
	{

		// prepare stdin receiver
		{
			new LineDispatcherInputStream(
				logger,
				new BufferedReader(new InputStreamReader(System.in)),
				new LineSource("STDIN", "blue"),
				new LineReceiverService(logger, this, in, out)).start();
			if (properties.getBoolean("plugin.web").get()) {
				new LineDispatcherWebInterface(
					logger,
					new LineReceiverService(logger, this, in, out),
					new LineSource("WEB", "green"),
					properties,
					lineStorage).start();
			}
			Thread thread = Thread.currentThread();
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				logger.log("Stopping...");
				restartable = false;
				Optional<Runner> oRunner2 = oRunner;
				if (oRunner2.isPresent()) {
					Optional<Process> oProcess = oRunner2.get().oProcess;
					if (oProcess.isPresent()) {
						oProcess.get().destroy();
					}
				}
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}));
		}

		// サービス名定義
		int serviceNumber = createServiceNumber();
		properties.put("service.number", "" + serviceNumber);
		String serviceId = properties.get("service.id.format");
		properties.put("service.id", serviceId);

		// ログファイルの設定
		File fileServiceLog = new File(properties.get("file.service.log"));
		fileServiceLog.getParentFile().mkdirs();
		try (PrintStream out = new PrintStream(new FileOutputStream(fileServiceLog))) {

			// サービス開始
			log(out, properties.get("message.service.start"));

			// ループ
			int sessionNumber = 0;
			do {
				try {
					session(sessionNumber, out);
				} catch (Exception e) {
					log(out, e);
				}
				sessionNumber++;
			} while (restartable);

		}

	}

	private void session(int sessionNumber, PrintStream out) throws Exception
	{

		// セッション名定義
		properties.put("session.number", "" + sessionNumber);
		String sessionId = properties.get("session.id.format");
		properties.put("session.id", sessionId);

		// セッション開始
		log(out, properties.get("message.session.start"));

		// 実行の設定
		String[] command = properties.get("command").split(" +");
		File currentDirectory = new File(properties.get("currentDirectory"));
		currentDirectory.mkdirs();

		// 実行
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(currentDirectory);
		Process process = processBuilder.start();

		// 入出力
		String encoding = properties.get("encoding");
		if (encoding.isEmpty()) encoding = Charset.defaultCharset().name();
		PrintStream stdin = new PrintStream(process.getOutputStream(), true, encoding);
		BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), encoding));
		BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream(), encoding));

		new Thread(() -> {
			while (true) {
				stdin.println(Math.random());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
			}
		}).start();

		new Thread(() -> {
			while (true) {
				try {
					log(out, stdout.readLine());
				} catch (IOException e) {
					break;
				}
			}
		}).start();

		new Thread(() -> {
			while (true) {
				try {
					log(out, stderr.readLine());
				} catch (IOException e) {
					break;
				}
			}
		}).start();

		// 終了待機
		process.waitFor();

	}

	private void log(PrintStream out, String line)
	{
		if (!line.isEmpty()) {
			System.out.println(line);
			out.println("[STDOUT] " + line);
		}
	}

	private void log(PrintStream out, Exception e)
	{
		StringWriter out2 = new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		Stream.of(out2.toString().split("\r\n|[\r\n]")).forEach(l -> {
			System.err.println(l);
			out.println("[STDERR] " + l);
		});
	}

	private int createServiceNumber() throws InterruptedException
	{
		File fileLock = new File(properties.get("file.lock"));

		int serviceNumber = 0;
		while (true) {
			fileLock.getParentFile().mkdirs();
			try (FileChannel channel = FileChannel.open(
				fileLock.toPath(),
				StandardOpenOption.CREATE,
				StandardOpenOption.WRITE);
				FileLock lock = channel.tryLock()) {

				File fileServiceNumber = new File(properties.get("file.service.number"));
				fileServiceNumber.getParentFile().mkdirs();

				// サービス番号の取得
				if (fileServiceNumber.exists()) {
					try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileServiceNumber)))) {
						String line = in.readLine();
						if (line != null && !line.isEmpty()) {
							serviceNumber = Integer.parseInt(line, 10);
						}
					}
				}

				// サービス番号の書き出し
				try (PrintStream out = new PrintStream(new FileOutputStream(fileServiceNumber))) {
					out.println(serviceNumber + 1);
				}

				break;
			} catch (IOException e) {

			}
			Thread.sleep(100);
		}

		return serviceNumber;
	}

	public static final DateTimeFormatter FORMATTER_SESSION_ID = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

	public static String getTimeId()
	{
		return LocalDateTime.now().format(FORMATTER_SESSION_ID);
	}

}
