package mirrg.lithium.event;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IEventRegistry<T>
{

	/**
	 * 登録した順番に呼び出されます。
	 *
	 * @return listenerが返されます。
	 */
	public default <E extends T, R extends Consumer<E>> R register(Class<E> clazz, R listener)
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
	public default <E extends T, R extends IConsumer<E>> R registerThrowable(Class<E> clazz, R listener)
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
	public default <E extends T, R extends Predicate<E>> R registerRemovable(Class<E> clazz, R listener)
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
	public default <E extends T, R extends IPredicate<E>> R registerRemovableThrowable(Class<E> clazz, R listener)
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
	public default <E extends T, R> R register(Class<E> clazz, Predicate<E> listener, R remover)
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
	public <E extends T, R> R registerThrowable(Class<E> clazz, IPredicate<E> listener, R remover);

	/**
	 * 登録済みのイベントハンドラを削除します。
	 */
	public <E extends T> void remove(Object remover);

	public static interface IConsumer<T>
	{

		public void accept(T t) throws Exception;

	}

	public static interface IPredicate<T>
	{

		public boolean test(T t) throws Exception;

	}

}
