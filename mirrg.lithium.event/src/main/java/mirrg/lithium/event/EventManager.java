package mirrg.lithium.event;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * とてもシンプルなイベントマネージャです。
 */
public class EventManager<T>
{

	private ArrayList<Class<? extends T>> classes = new ArrayList<>();
	private ArrayList<IPredicate<? extends T>> listeners = new ArrayList<>();
	private ArrayList<Object> removers = new ArrayList<>();

	/**
	 * 登録した順番に呼び出されます。
	 *
	 * @return listenerが返されます。
	 */
	public <E extends T, R extends Consumer<E>> R register(Class<E> clazz, R listener)
	{
		return registerThrowable(clazz, e -> {
			listener.accept(e);
			return true;
		}, listener);
	}

	/**
	 * 登録した順番に呼び出されます。
	 *
	 * @return listenerが返されます。
	 */
	public <E extends T, R extends IConsumer<E>> R registerThrowable(Class<E> clazz, R listener)
	{
		return registerThrowable(clazz, e -> {
			listener.accept(e);
			return true;
		}, listener);
	}

	/**
	 * 登録した順番に呼び出されます。
	 *
	 * @param listener
	 *            falseを返した場合、このイベントハンドラは無効となります。
	 * @return listenerが返されます。
	 */
	public <E extends T, R extends Predicate<E>> R registerRemovable(Class<E> clazz, R listener)
	{
		return registerThrowable(clazz, listener::test, listener);
	}

	/**
	 * 登録した順番に呼び出されます。
	 *
	 * @param listener
	 *            falseを返した場合、このイベントハンドラは無効となります。
	 * @return listenerが返されます。
	 */
	public <E extends T, R extends IPredicate<E>> R registerRemovableThrowable(Class<E> clazz, R listener)
	{
		return registerThrowable(clazz, listener, listener);
	}

	/**
	 * 登録した順番に呼び出されます。
	 *
	 * @param listener
	 *            falseを返した場合、このイベントハンドラは無効となります。
	 * @param remover
	 *            イベントハンドラの削除時に指定するオブジェクトです。
	 * @return removerが返されます。
	 */
	public <E extends T, R> R register(Class<E> clazz, Predicate<E> listener, R remover)
	{
		return registerThrowable(clazz, listener::test, remover);
	}

	/**
	 * 登録した順番に呼び出されます。
	 *
	 * @param listener
	 *            falseを返した場合、このイベントハンドラは無効となります。
	 * @param remover
	 *            イベントハンドラの削除時に指定するオブジェクトです。
	 * @return removerが返されます。
	 */
	public <E extends T, R> R registerThrowable(Class<E> clazz, IPredicate<E> listener, R remover)
	{
		classes.add(clazz);
		listeners.add(listener);
		removers.add(remover);
		return remover;
	}

	/**
	 * 登録済みのイベントハンドラを削除します。
	 */
	public <E extends T> void remove(Object remover)
	{
		for (int i = 0; i < classes.size(); i++) {
			if (removers.get(i).equals(remover)) {
				classes.remove(i);
				listeners.remove(i);
				removers.remove(i);
				i--;
			}
		}
	}

	/**
	 * 登録時のクラスがこのイベントと代入互換であるハンドラを、
	 * 登録時の順番に起動します。
	 */
	@SuppressWarnings("unchecked")
	public <E extends T> void post(E event)
	{
		for (int i = 0; i < classes.size(); i++) {
			if (classes.get(i).isInstance(event)) {
				boolean flag = true;
				try {
					flag = ((IPredicate<E>) listeners.get(i)).test(event);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!flag) {
					classes.remove(i);
					listeners.remove(i);
					removers.remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * 登録時のクラスがこのイベントと代入互換であるハンドラを、
	 * 登録時の順番に起動します。
	 *
	 * @param consumer
	 *            falseを返した場合、後続のイベントがキャンセルされます。
	 */
	@SuppressWarnings("unchecked")
	public <E extends T> void post(E event, Predicate<Exception> consumer)
	{
		for (int i = 0; i < classes.size(); i++) {
			if (classes.get(i).isInstance(event)) {
				boolean flag = true;
				try {
					flag = ((IPredicate<E>) listeners.get(i)).test(event);
				} catch (Exception e) {
					if (!consumer.test(e)) {
						break;
					}
				}
				if (!flag) {
					classes.remove(i);
					listeners.remove(i);
					removers.remove(i);
					i--;
				}
			}
		}
	}

	public static interface IConsumer<T>
	{

		public void accept(T t) throws Exception;

	}

	public static interface IPredicate<T>
	{

		public boolean test(T t) throws Exception;

	}

}
