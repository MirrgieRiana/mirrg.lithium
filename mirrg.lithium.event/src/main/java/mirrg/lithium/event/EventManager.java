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
	private ArrayList<Predicate<? extends T>> listeners = new ArrayList<>();
	private ArrayList<Object> removers = new ArrayList<>();

	/**
	 * 登録した順番に呼び出されます。
	 *
	 * @return listenerが返されます。
	 */
	public <E extends T, L extends Consumer<E>> L register(Class<E> clazz, L listener)
	{
		register(clazz, e -> {
			listener.accept(e);
			return true;
		}, listener);
		return listener;
	}

	/**
	 * 登録した順番に呼び出されます。
	 *
	 * @param listener
	 *            falseを返した場合、このイベントハンドラは無効となります。
	 * @return listenerが返されます。
	 */
	public <E extends T, L extends Predicate<E>> L registerRemovable(Class<E> clazz, L listener)
	{
		register(clazz, listener, listener);
		return listener;
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
	public <E extends T> Object register(Class<E> clazz, Predicate<E> listener, Object remover)
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
				if (!((Predicate<E>) listeners.get(i)).test(event)) {
					classes.remove(i);
					listeners.remove(i);
					removers.remove(i);
					i--;
				}
			}
		}
	}

}