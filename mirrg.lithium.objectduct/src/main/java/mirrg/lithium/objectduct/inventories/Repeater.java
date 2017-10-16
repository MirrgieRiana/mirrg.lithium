package mirrg.lithium.objectduct.inventories;

import mirrg.lithium.objectduct.Terminal;
import mirrg.lithium.objectduct.TerminalClosedException;

/**
 * 単一のインポーターおよびエクスポーターを持ち、
 * 入力されたオブジェクトをそのまま出力するインベントリです。
 */
public class Repeater<T> extends Objectduct
{

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
			protected void acceptImpl(T t) throws InterruptedException, TerminalClosedException
			{
				exporter.accept(t);
			}

			@Override
			protected void closeImpl()
			{
				exporter.close();
			}

		};
	}

}
