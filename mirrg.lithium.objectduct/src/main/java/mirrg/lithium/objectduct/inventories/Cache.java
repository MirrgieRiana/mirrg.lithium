package mirrg.lithium.objectduct.inventories;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import mirrg.lithium.objectduct.Terminal;

/**
 * 単一のインポーターを持ち、
 * 直近で入力された指定個数のオブジェクトを取得できるインベントリです。
 * バッファがあふれる場合、古い順に捨てられます。
 */
public class Cache<T> extends Objectduct
{

	private int bufferSize;
	private ArrayDeque<T> queue = new ArrayDeque<>();
	private Object lock = new Object();

	public Cache(int bufferSize)
	{
		this.bufferSize = bufferSize;
	}

	//

	private Terminal<T> importer;

	public Terminal<T> getImporter()
	{
		return importer;
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
				synchronized (lock) {
					while (queue.size() >= bufferSize) {
						queue.removeFirst();
					}
					queue.addLast(t);
				}
			}

			@Override
			protected void closeImpl()
			{

			}

		};
	}

	//

	/**
	 * 直近に入力された指定個数のオブジェクトを取得します。
	 * オブジェクトは古い順に並びます。
	 */
	public T[] toArray(IntFunction<T[]> fArray)
	{
		synchronized (lock) {
			T[] array = fArray.apply(queue.size());
			queue.toArray(array);
			return array;
		}
	}

	/**
	 * 直近に入力された指定個数のオブジェクトを取得します。
	 * オブジェクトは古い順に並びます。
	 */
	public <T2 extends Collection<T>> T2 toCollection(Supplier<T2> sCollection)
	{
		T2 t2 = sCollection.get();
		synchronized (lock) {
			t2.addAll(queue);
		}
		return t2;
	}

}
