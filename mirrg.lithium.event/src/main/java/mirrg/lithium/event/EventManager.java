package mirrg.lithium.event;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * とてもシンプルなイベントマネージャです。
 */
public class EventManager<T> implements IEventRegistry<T>
{

	private ArrayList<Class<? extends T>> classes = new ArrayList<>();
	private ArrayList<IPredicate<? extends T>> listeners = new ArrayList<>();
	private ArrayList<Object> removers = new ArrayList<>();

	@Override
	public <E extends T, R> R registerThrowable(Class<E> clazz, IPredicate<E> listener, R remover)
	{
		classes.add(clazz);
		listeners.add(listener);
		removers.add(remover);
		return remover;
	}

	@Override
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

}
