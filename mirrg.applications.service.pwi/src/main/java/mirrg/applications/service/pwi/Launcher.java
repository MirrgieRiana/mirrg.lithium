package mirrg.applications.service.pwi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;

import mirrg.applications.service.pwi.BlockWeb.WebSetting;
import mirrg.lithium.lang.HString;
import mirrg.lithium.properties.IProperties;
import mirrg.lithium.properties.PropertiesMultipleInheritable;
import mirrg.lithium.properties.PropertyBasic;
import mirrg.lithium.struct.Struct1;

public class Launcher
{

	public PropertiesMultipleInheritable properties;

	public volatile boolean restartable;
	public int serviceNumber;
	public String serviceId;
	public volatile Optional<Session> oSession = Optional.empty();

	public static class Session
	{

		public int sessionNumber;
		public String sessionId;
		public String encoding;
		public String[] command;
		public File currentDirectory;
		public Process process;

	}

	public LineBuffer lineBufferServiceIn;
	public LineBuffer lineBufferPreSession;
	public LineBuffer lineBufferServiceOut;
	public LineStorage lineStorageWeb;

	public Logger logger;

	public Launcher(IProperties properties)
	{
		this.properties = new PropertiesMultipleInheritable();
		this.properties.addParent(properties);
	}

	public void run() throws Exception
	{

		// プロパティメソッドの設定
		properties.put("time.id", p -> new PropertyBasic(getTimeId()));
		properties.put("service.number", p -> new PropertyBasic("" + serviceNumber));
		properties.put("service.number.id", p -> new PropertyBasic(HString.fillLeft('0', p.get("service.number").getString().get(), 5)));
		properties.put("service.id", p -> new PropertyBasic(serviceId));
		properties.put("session.number", p -> new PropertyBasic(oSession.map(s -> "" + s.sessionNumber).orElse("undefined")));
		properties.put("session.number.id", p -> new PropertyBasic(HString.fillLeft('0', p.get("session.number").getString().get(), 5)));
		properties.put("session.id", p -> new PropertyBasic("" + oSession.map(s -> s.sessionId).orElse("undefined")));

		// 各種変数の初期化
		restartable = properties.get("restartable").getBoolean().get();
		serviceNumber = createServiceNumber();
		serviceId = properties.get("service.id.format").getString().get();
		File fileServiceLog = new File(properties.get("file.service.log").getString().get());

		////////

		lineBufferServiceIn = new LineBuffer();
		lineBufferPreSession = new LineBuffer();
		lineBufferServiceOut = new LineBuffer();
		lineStorageWeb = new LineStorage(properties.get("log.lines").getInteger().get());

		logger = new Logger(lineBufferServiceOut, new LineSource("SERVICE", "magenta"));

		Struct1<PrintStream> outServiceLog = new Struct1<>();

		// シャットダウンシグナルか終了時にで子プロセスを殺す
		Thread currentThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.log("Stopping...");
			restartable = false;
			oSession.ifPresent(s -> s.process.destroy());
			try {
				currentThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}));

		//

		logger.log(properties.get("message.service.start").getString().get());

		// 各種スレッド等構築
		Consumer<Runnable> frame = Runnable::run;
		{

			// サービスログの出力ストリーム
			frame = createFrame(frame, () -> {
				fileServiceLog.getParentFile().mkdirs();
				return outServiceLog.x = new PrintStream(new FileOutputStream(fileServiceLog));
			});

			// 標準入力から読み込んでserviceInに送る
			frame = createFrame(frame, () -> new BlockBufferedReaderToReceiver(logger,
				new BufferedReader(new InputStreamReader(System.in)),
				new LineSource("STDIN", "blue"),
				lineBufferServiceIn).start(true));

			// webから読み込んでserviceInに送る
			if (properties.get("plugin.web").getBoolean().get()) {
				frame = createFrame(frame, () -> new BlockWeb(logger,
					new WebSetting() {
						{
							host = properties.get("plugin.web.host").getString().get();
							port = properties.get("plugin.web.port").getInteger().get();
							backlog = properties.get("plugin.web.backlog").getInteger().get();

							homeDirectory = parseHomeDirectory(properties.get("plugin.web.homeDirectory").getString().get());
							cgiSettings = parseCgiSettings(properties.get("plugin.web.cgi").getString().get());
							indexes = parseIndexes(properties.get("plugin.web.indexes").getString().get());

							timeoutMs = properties.get("plugin.web.timeoutMs").getInteger().get();
							requestBufferSize = properties.get("plugin.web.requestBufferSize").getInteger().get();
							responseBufferSize = properties.get("plugin.web.responseBufferSize").getInteger().get();

							needAuthentication = properties.get("plugin.web.needAuthentication").getBoolean().get();
							basicAuthenticationRegex = properties.get("plugin.web.basicAuthenticationRegex").getString().get();

							lineStorage = lineStorageWeb;
						}
					},
					new LineSource("WEB", "green"),
					lineBufferServiceIn).start(true));
			}

			// serviceInから読み込んでコマンド解析してpreSessinとserviceOutに送る
			frame = createFrame(frame, () -> new BlockCommand(logger,
				lineBufferServiceIn,
				new LineSource("COMMAND", "orange"),
				this,
				lineBufferPreSession,
				lineBufferServiceOut).start(true));

			// serviceOutから読み込んで標準出力とlineStorageWebに送る
			frame = createFrame(frame, () -> new BlockLineBufferRedirection(logger, lineBufferServiceOut)
				.addLineConsumer(lineStorageWeb)
				.addStringConsumer(System.out::println)
				.addStringConsumer(outServiceLog.x::println)
				.start(true));

		}

		//////// start ////////

		frame.accept(this::loop);

	}

	private void loop()
	{
		int sessionNumber = 0;
		do {

			try {
				session(sessionNumber);
			} catch (Exception e) {
				logger.log(e);
			}

			sessionNumber++;
		} while (restartable);
	}

	private void session(int sessionNumber) throws Exception
	{

		// 各種変数の初期化
		Session session = new Session();
		oSession = Optional.of(session);
		session.sessionNumber = sessionNumber;
		session.sessionId = properties.get("session.id.format").getString().get();
		session.encoding = properties.get("encoding").getString().get();
		if (session.encoding.isEmpty()) session.encoding = Charset.defaultCharset().name();

		logger.log(properties.get("message.session.start").getString().get());

		////////

		// 実行の設定
		session.command = properties.get("command").getString().get().split(" +");
		session.currentDirectory = new File(properties.get("currentDirectory").getString().get());
		session.currentDirectory.mkdirs();

		// 実行
		ProcessBuilder processBuilder = new ProcessBuilder(session.command);
		processBuilder.directory(session.currentDirectory);
		session.process = processBuilder.start();

		// 各種スレッド等構築
		Consumer<Runnable> frame = Runnable::run;
		{

			// preSessionから読み込んで子プロセスの標準入力に送る
			frame = createFrame(frame, () -> new BlockLineBufferRedirection(logger, lineBufferPreSession)
				.addRawStringConsumer(new PrintStream(session.process.getOutputStream(), true, session.encoding)::println)
				.start(true));

			// 子プロセスの標準出力から読み込んでserviceOutに送る
			frame = createFrame(frame, () -> new BlockBufferedReaderToReceiver(logger,
				new BufferedReader(new InputStreamReader(session.process.getInputStream(), session.encoding)),
				new LineSource("STDOUT", "black"),
				lineBufferServiceOut).start(true));

			// 子プロセスの標準エラー出力から読み込んでserviceOutに送る
			frame = createFrame(frame, () -> new BlockBufferedReaderToReceiver(logger,
				new BufferedReader(new InputStreamReader(session.process.getErrorStream(), session.encoding)),
				new LineSource("STDERR", "red"),
				lineBufferServiceOut).start(true));

		}

		//////// start ////////

		frame.accept(() -> {

			// 終了待機
			try {
				session.process.waitFor();
			} catch (InterruptedException e) {

			}

			oSession = Optional.empty();

		});

	}

	private Consumer<Runnable> createFrame(Consumer<Runnable> frame, IAutoCloseableSupplier sAutoCloseable)
	{
		return y -> {
			frame.accept(() -> {
				try (AutoCloseable autoCloseable = sAutoCloseable.get()) {
					y.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		};
	}

	private static interface IAutoCloseableSupplier
	{

		public AutoCloseable get() throws Exception;

	}

	private int createServiceNumber() throws InterruptedException
	{
		File fileLock = new File(properties.get("file.lock").getString().get());

		int serviceNumber = 0;
		while (true) {
			fileLock.getParentFile().mkdirs();
			try (FileChannel channel = FileChannel.open(
				fileLock.toPath(),
				StandardOpenOption.CREATE,
				StandardOpenOption.WRITE);
				FileLock lock = channel.tryLock()) {

				File fileServiceNumber = new File(properties.get("file.service.number").getString().get());
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
