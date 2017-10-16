package mirrg.lithium.objectduct.inventories;

import java.util.ArrayList;

import mirrg.lithium.objectduct.Terminal;
import mirrg.lithium.objectduct.TerminalClosedException;

/**
 * 単一のインポーターからの入力を複数のエクスポーターに出力するインベントリです。
 */
public class ExportBus<T> extends Objectduct
{

	private Terminal<T> importer;
	private ArrayList<Terminal<T>> exporters = new ArrayList<>();

	public Terminal<T> getImporter()
	{
		return importer;
	}

	public void addExporter(Terminal<T> exporter)
	{
		exporters.add(exporter);
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
				for (Terminal<T> exporter : exporters) {
					exporter.accept(t);
				}
			}

			@Override
			protected void closeImpl()
			{
				for (Terminal<T> exporter : exporters) {
					exporter.close();
				}
			}

		};
	}

}
