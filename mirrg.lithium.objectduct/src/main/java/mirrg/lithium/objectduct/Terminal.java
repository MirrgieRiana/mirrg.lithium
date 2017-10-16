package mirrg.lithium.objectduct;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Terminal<T>
{

	private Object lock = new Object();
	private Object lock_event_join = new Object();
	private volatile boolean closed = false;

	/**
	 * {@link #close()} の呼び出し後は呼び出すことができません。
	 */
	public void accept(T t) throws InterruptedException, TerminalClosedException
	{
		synchronized (lock) {
			if (closed) throw new TerminalClosedException("Illegal Object: " + t);
			acceptImpl(t);
		}
	}

	/**
	 * {@link #acceptImpl(Object)} および {@link #closeImpl()} とは排他制御されており、
	 * 同時に呼び出されることはありません。
	 * これらのメソッドは {@link Terminal} 自身からのみ呼び出すことが許されます。
	 */
	protected abstract void acceptImpl(T t) throws InterruptedException, TerminalClosedException;

	/**
	 * 2度目以降の呼び出しは単に無視されます。
	 */
	public void close()
	{
		synchronized (lock) {
			if (closed) return;
			closed = true;
			closeImpl();
		}
		synchronized (lock_event_join) {
			lock_event_join.notifyAll();
		}
	}

	/**
	 * {@link #acceptImpl(Object)} および {@link #closeImpl()} とは排他制御されており、
	 * 同時に呼び出されることはありません。
	 * これらのメソッドは {@link Terminal} 自身からのみ呼び出すことが許されます。
	 */
	protected abstract void closeImpl();

	public boolean isClosed()
	{
		return closed;
	}

	/**
	 * この端末が閉じられるまで待機します。
	 */
	public void join() throws InterruptedException
	{
		synchronized (lock_event_join) {
			while (!isClosed()) {
				lock_event_join.wait();
			}
		}
	}

	//

	public <T2> Terminal<T2> map(Function<T2, T> function)
	{
		return new Terminal<T2>() {

			@Override
			protected void acceptImpl(T2 t) throws InterruptedException, TerminalClosedException
			{
				Terminal.this.accept(function.apply(t));
			}

			@Override
			protected void closeImpl()
			{
				Terminal.this.close();
			}

		};
	}

	public Terminal<T> filter(Predicate<T> predicate)
	{
		return new Terminal<T>() {

			@Override
			protected void acceptImpl(T t) throws InterruptedException, TerminalClosedException
			{
				if (predicate.test(t)) Terminal.this.accept(t);
			}

			@Override
			protected void closeImpl()
			{
				Terminal.this.close();
			}

		};
	}

}
