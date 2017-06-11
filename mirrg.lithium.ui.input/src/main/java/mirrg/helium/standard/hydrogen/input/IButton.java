package mirrg.helium.standard.hydrogen.input;

public interface IButton
{

	/**
	 * まだ初期化されていないときに真を返す。
	 */
	public default boolean isUndefined()
	{
		return getState() == 0;
	}

	/**
	 * 押しているときに真を返す。
	 */
	public default boolean isPressing()
	{
		return getState() > 0;
	}

	/**
	 * 離しているときに真を返す。
	 */
	public default boolean isReleasing()
	{
		return getState() < 0;
	}

	/**
	 * 押しているときに一定間隔で真を返す。
	 */
	public default boolean isPressing(int interval)
	{
		return isPressing() && (getState() - 1) % interval == 0;
	}

	/**
	 * 離しているときに一定間隔で真を返す。
	 */
	public default boolean isReleasing(int interval)
	{
		return isReleasing() && (-getState() - 1) % interval == 0;
	}

	/**
	 * 押した瞬間に真を返す。
	 */
	public default boolean isPressed()
	{
		return getState() == 1;
	}

	/**
	 * 離した瞬間に真を返す。
	 */
	public default boolean isReleased()
	{
		return getState() == -1;
	}

	public int getState();

}
