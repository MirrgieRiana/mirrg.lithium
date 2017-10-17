package mirrg.lithium.objectduct.inventories;

import java.util.ArrayList;

import mirrg.lithium.objectduct.IInventory;

public abstract class Objectduct implements IInventory
{

	private ArrayList<IInventory> inventories = new ArrayList<>();

	public void add(IInventory inventory)
	{
		inventories.add(inventory);
	}

	@Override
	public void init() throws Exception
	{
		initInventories();
		for (IInventory inventory : inventories) {
			inventory.init();
		}
		initConnections();
	}

	protected abstract void initInventories() throws Exception;

	protected abstract void initConnections() throws Exception;

	@Override
	public void start() throws Exception
	{
		for (IInventory inventory : inventories) {
			inventory.start();
		}
	}

	@Override
	public void stop() throws Exception
	{
		for (IInventory inventory : inventories) {
			inventory.stop();
		}
	}

	@Override
	public void join() throws InterruptedException
	{
		for (IInventory inventory : inventories) {
			inventory.join();
		}
	}

}
