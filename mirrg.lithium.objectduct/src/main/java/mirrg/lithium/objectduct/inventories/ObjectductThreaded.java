package mirrg.lithium.objectduct.inventories;

/**
 * 単一のインポーターおよびエクスポーターを持ち、
 * 入力されたオブジェクトを一旦決められた最大容量のバッファに保管し、
 * ディスパッチスレッドにより自動的にオブジェクトを出力するインベントリです。
 */
public abstract class ObjectductThreaded<T> extends Objectduct
{

	private Object lock = new Object();
	private Thread thread = null;

	//

	@Override
	public void start() throws Exception
	{
		super.start();
		up();
	}

	@Override
	public void stop()
	{
		super.stop();
		try {
			down();
		} catch (InterruptedException e) {

		}
	}

	/**
	 * オートメーションが実行中でない場合、オートメーションを開始します。
	 */
	public void up()
	{
		synchronized (lock) {
			if (thread == null) {
				thread = createThread();
				thread.start();
			}
		}
	}

	protected String getThreadName()
	{
		return getClass().getSimpleName();
	}

	protected Thread createThread()
	{
		return new Thread(this::run, getThreadName());
	}

	protected void run()
	{
		try {
			runImpl();
		} catch (InterruptedException e) {

		} finally {
			synchronized (lock) {
				thread = null;
			}
		}
	}

	protected abstract void runImpl() throws InterruptedException;

	/**
	 * オートメーションが実行中である場合、中断命令を出し、停止するまで待機します。
	 */
	public void down() throws InterruptedException
	{
		Thread thread2;
		synchronized (lock) {
			if (thread == null) return;
			thread2 = thread;
		}
		thread2.interrupt();
		thread2.join();
	}

	/**
	 * オートメーションが実行中であるか否かを返します。
	 */
	public boolean isRunning()
	{
		synchronized (lock) {
			return thread != null;
		}
	}

	/**
	 * オートメーションが停止するまで待機します。
	 */
	@Override
	public void join() throws InterruptedException
	{
		super.join();

		Thread thread2;
		synchronized (lock) {
			if (thread == null) return;
			thread2 = thread;
		}
		thread2.join();
	}

}
