package mirrg.lithium.objectduct.inventories;

import java.util.HashSet;

import mirrg.lithium.objectduct.Terminal;
import mirrg.lithium.objectduct.TerminalClosedException;

/**
 * 複数のインポーターを持ち、それぞれからの入力を排他制御して
 * 単一のエクスポーターに出力するインベントリです。
 */
public class ImportBus<T> extends Objectduct
{

	private Object lock = new Object();
	private boolean closed = false;

	//

	private HashSet<Terminal<T>> importers = new HashSet<>();
	private Terminal<T> exporter;

	public Terminal<T> addImporter()
	{
		return new Terminal<T>() {

			{
				synchronized (lock) {
					importers.add(this);
				}
			}

			@Override
			protected void acceptImpl(T t) throws InterruptedException, TerminalClosedException
			{
				exporter.accept(t);
			}

			@Override
			protected void closeImpl()
			{
				synchronized (lock) {
					importers.remove(this);
				}
				update();
			}

		};
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

	}

	@Override
	public void start() throws Exception
	{
		super.start();
		update();
	}

	private void update()
	{
		synchronized (lock) {
			if (closed) return;
			if (!importers.isEmpty()) return;
			closed = true;
		}
		exporter.close();
	}

}
