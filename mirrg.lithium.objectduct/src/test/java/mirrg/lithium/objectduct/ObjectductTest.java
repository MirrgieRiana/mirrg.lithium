package mirrg.lithium.objectduct;

import mirrg.lithium.objectduct.TerminalClosedException;
import mirrg.lithium.objectduct.inventories.Objectduct;

abstract class ObjectductTest extends Objectduct
{

	abstract void startFlow() throws InterruptedException, TerminalClosedException;

	abstract void test() throws Exception;

	void run() throws Exception
	{
		init();
		start();
		startFlow();
		join();
		test();
	}

}
