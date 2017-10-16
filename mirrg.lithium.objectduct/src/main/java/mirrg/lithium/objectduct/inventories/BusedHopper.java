package mirrg.lithium.objectduct.inventories;

/**
 * 複数の入力と出力に対応したホッパーです。
 */
public class BusedHopper<T> extends Objectduct
{

	private int bufferSize;

	public BusedHopper(int bufferSize)
	{
		this.bufferSize = bufferSize;
	}

	//

	private ImportBus<T> importBus;
	private Hopper<T> hopper;
	private ExportBus<T> exportBus;

	public ImportBus<T> getImportBus()
	{
		return importBus;
	}

	public ExportBus<T> getExportBus()
	{
		return exportBus;
	}

	@Override
	protected void initInventories()
	{
		add(importBus = new ImportBus<>());
		add(hopper = new Hopper<>(bufferSize));
		add(exportBus = new ExportBus<>());
	}

	@Override
	protected void initConnections()
	{
		importBus.setExporter(hopper.getImporter());
		hopper.setExporter(exportBus.getImporter());
	}

}
