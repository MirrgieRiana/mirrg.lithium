package mirrg.lithium.objectduct.inventories;

import java.util.ArrayDeque;

import mirrg.lithium.objectduct.Terminal;
import mirrg.lithium.objectduct.TerminalClosedException;

/**
 * 単一のインポーターおよびエクスポーターを持ち、
 * 入力されたオブジェクトを一旦決められた最大容量のバッファに保管し、
 * ディスパッチスレッドにより自動的にオブジェクトを出力するインベントリです。
 */
public class Hopper<T> extends ObjectductThreaded<T>
{

	private int bufferSize;
	private ArrayDeque<T> queue = new ArrayDeque<>();
	private Object lock_event_dispatchThread = new Object();
	private Object lock_event_accept = new Object();

	public Hopper(int bufferSize)
	{
		this.bufferSize = bufferSize;
	}

	//

	private Terminal<T> importer;
	private Terminal<T> exporter;

	public Terminal<T> getImporter()
	{
		return importer;
	}

	public void setExporter(Terminal<T> exporter)
	{
		this.exporter = exporter;
	}

	@Override
	protected void initInventories()
	{

	}

	@Override
	protected void initConnections()
	{
		importer = new Terminal<T>() {

			@Override
			protected void acceptImpl(T t) throws InterruptedException
			{
				synchronized (lock_event_accept) {
					while (queue.size() >= bufferSize) {
						lock_event_accept.wait();
					}
					queue.addLast(t);
				}

				synchronized (lock_event_dispatchThread) {
					lock_event_dispatchThread.notifyAll();
				}
			}

			@Override
			protected void closeImpl()
			{
				synchronized (lock_event_dispatchThread) {
					lock_event_dispatchThread.notifyAll();
				}
			}

		};
	}

	@Override
	protected void runImpl() throws InterruptedException
	{
		try {
			while (true) {
				T t;

				synchronized (lock_event_dispatchThread) {
					while (true) {
						if (!queue.isEmpty()) break;
						if (importer.isClosed()) return;
						lock_event_dispatchThread.wait();
					}
					t = queue.removeFirst();
				}

				synchronized (lock_event_accept) {
					lock_event_accept.notifyAll();
				}

				exporter.accept(t);
			}
		} catch (TerminalClosedException e) {
			throw new RuntimeException(e);
		} finally {
			exporter.close();
		}
	}

}
