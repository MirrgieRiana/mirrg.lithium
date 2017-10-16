package mirrg.lithium.objectduct;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.junit.Test;

import mirrg.lithium.objectduct.Terminal;
import mirrg.lithium.objectduct.TerminalClosedException;
import mirrg.lithium.objectduct.inventories.Cache;
import mirrg.lithium.objectduct.inventories.ImportBus;

public class TestCache
{

	@Test
	public void test_cache() throws Exception
	{
		test_cache1(5, 10, () -> new Cache<>(10));
		test_cache1(100, 10, () -> new Cache<>(10));
		test_cache1(10000, 100, () -> new Cache<>(100));
	}

	private abstract class ObjectductCache extends ObjectductTest
	{

		ImportBus<Integer> importBus;
		Cache<Integer> cache;

		Supplier<Cache<Integer>> sCache;

		ObjectductCache(Supplier<Cache<Integer>> sCache)
		{
			this.sCache = sCache;
		}

		@Override
		protected void initInventories()
		{
			add(importBus = new ImportBus<>());
			add(cache = sCache.get());
		}

		@Override
		protected void initConnections()
		{
			importBus.setExporter(cache.getImporter());
		}

	}

	public void test_cache1(int integers, int bufferSize, Supplier<Cache<Integer>> sCache) throws Exception
	{
		new ObjectductCache(sCache) {

			ArrayList<Integer> in = new ArrayList<Integer>() {
				{
					for (int i = 0; i < integers; i++) {
						add(i);
					}
				}
			};
			ArrayList<Integer> expected = new ArrayList<Integer>() {
				{
					for (int i = Math.max(integers - bufferSize, 0); i < integers; i++) {
						add(i);
					}
				}
			};

			Terminal<Integer> importer;

			@Override
			protected void initConnections()
			{
				super.initConnections();
				importer = importBus.addImporter();
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
				assertEquals(expected, cache.toCollection(ArrayList::new));
			}

		}.run();
	}

}
