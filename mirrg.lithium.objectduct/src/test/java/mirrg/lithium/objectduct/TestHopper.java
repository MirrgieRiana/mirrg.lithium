package mirrg.lithium.objectduct;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.junit.Test;

import mirrg.lithium.objectduct.Terminal;
import mirrg.lithium.objectduct.TerminalClosedException;
import mirrg.lithium.objectduct.inventories.ExportBus;
import mirrg.lithium.objectduct.inventories.Hopper;
import mirrg.lithium.objectduct.inventories.ImportBus;

public class TestHopper
{

	@Test
	public void test_hopper() throws Exception
	{
		test_hopper(3, () -> new Hopper<>(10));
		test_hopper(100, () -> new Hopper<>(10));
		test_hopper(10000, () -> new Hopper<>(10));
		for (int i = 0; i < 100; i++) {
			test_hopper(100, () -> new Hopper<>(10));
		}
	}

	public void test_hopper(int integers, Supplier<Hopper<Integer>> sHopper) throws Exception
	{
		test_hopper1(integers, sHopper);
		test_hopper2(sHopper);
		test_hopper3(sHopper);
	}

	//

	private abstract class ObjectductHopper extends ObjectductTest
	{

		ImportBus<Integer> importBus;
		Hopper<Integer> hopper;
		ExportBus<Integer> exportBus;

		Supplier<Hopper<Integer>> sHopper;

		ObjectductHopper(Supplier<Hopper<Integer>> sHopper)
		{
			this.sHopper = sHopper;
		}

		@Override
		protected void initInventories()
		{
			add(importBus = new ImportBus<>());
			add(hopper = sHopper.get());
			add(exportBus = new ExportBus<>());
		}

		@Override
		protected void initConnections()
		{
			importBus.setExporter(hopper.getImporter());
			hopper.setExporter(exportBus.getImporter());
		}

	}

	public void test_hopper1(int integers, Supplier<Hopper<Integer>> sHopper) throws Exception
	{
		new ObjectductHopper(sHopper) {

			ArrayList<Integer> in = new ArrayList<Integer>() {
				{
					for (int i = 0; i < integers; i++) {
						add(i);
					}
				}
			};
			ArrayList<Integer> out = new ArrayList<>();
			ArrayList<Integer> expected = new ArrayList<Integer>() {
				{
					for (int i = 0; i < integers; i++) {
						add(i);
					}
					add(-1);
				}
			};

			Terminal<Integer> importer;

			@Override
			protected void initConnections()
			{
				super.initConnections();
				importer = importBus.addImporter();
				exportBus.addExporter(new Terminal<Integer>() {

					@Override
					protected void acceptImpl(Integer t) throws InterruptedException
					{
						out.add(t);
					}

					@Override
					protected void closeImpl()
					{
						out.add(-1);
					}

				});
			}

			@Override
			void startFlow() throws InterruptedException, TerminalClosedException
			{
				in.forEach(t -> {
					try {
						importer.accept(t);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					} catch (TerminalClosedException e) {
						throw new RuntimeException(e);
					}
				});
				importer.close();
			}

			@Override
			void test() throws Exception
			{
				assertEquals(expected, out);
			}

		}.run();
	}

	public void test_hopper2(Supplier<Hopper<Integer>> sHopper) throws Exception
	{
		new ObjectductHopper(sHopper) {

			ArrayList<Integer> out = new ArrayList<>();
			ArrayList<Integer> expected = new ArrayList<Integer>() {
				{
					add(1);
					add(2);
					add(10);
					add(20);
					add(3);
					add(30);
					add(-1);
				}
			};

			Terminal<Integer> importer1;
			Terminal<Integer> importer2;

			@Override
			protected void initConnections()
			{
				super.initConnections();
				importer1 = importBus.addImporter();
				importer2 = importBus.addImporter();
				exportBus.addExporter(new Terminal<Integer>() {

					@Override
					protected void acceptImpl(Integer t) throws InterruptedException
					{
						out.add(t);
					}

					@Override
					protected void closeImpl()
					{
						out.add(-1);
					}

				});
			}

			@Override
			void startFlow() throws InterruptedException, TerminalClosedException
			{
				importer1.accept(1);
				importer1.accept(2);
				importer2.accept(10);
				importer2.accept(20);
				importer1.accept(3);
				importer1.close();
				importer2.accept(30);
				importer2.close();
			}

			@Override
			void test() throws Exception
			{
				assertEquals(expected, out);
			}

		}.run();
	}

	public void test_hopper3(Supplier<Hopper<Integer>> sHopper) throws Exception
	{
		final int THREADS = 5;
		final int INTEGERS = 1000;

		new ObjectductHopper(sHopper) {

			ArrayList<Integer> out = new ArrayList<>();

			ArrayList<Terminal<Integer>> importers = new ArrayList<>();

			@Override
			protected void initConnections()
			{
				super.initConnections();
				for (int i = 0; i < THREADS; i++) {
					importers.add(importBus.addImporter());
				}
				exportBus.addExporter(new Terminal<Integer>() {

					@Override
					protected void acceptImpl(Integer t) throws InterruptedException
					{
						out.add(t);
					}

					@Override
					protected void closeImpl()
					{
						out.add(-1);
					}

				});
			}

			@Override
			void startFlow() throws InterruptedException, TerminalClosedException
			{
				ArrayList<Thread> threads = new ArrayList<>();
				for (int i = 0; i < THREADS; i++) {
					int index = i;
					threads.add(new Thread(() -> {
						for (int i2 = 0; i2 < INTEGERS; i2++) {
							try {
								importers.get(index).accept(i2);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							} catch (TerminalClosedException e) {
								throw new RuntimeException(e);
							}
						}
						importers.get(index).close();
					}));
				}
				for (Thread thread : threads) {
					thread.start();
				}
			}

			@Override
			void test() throws Exception
			{
				assertEquals(THREADS * (INTEGERS - 1) * INTEGERS / 2 - 1, out.stream()
					.mapToInt(i -> i)
					.sum());
			}

		};
	}

}
