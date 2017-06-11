package mirrg.helium.standard.hydrogen.input;

public interface IInputStatus
{

	/**
	 * Mouse X
	 */
	public int getX();

	/**
	 * Mouse Y
	 */
	public int getY();

	/**
	 * Mouse Wheel
	 */
	public int getW();

	/**
	 * Previous Mouse X
	 */
	public int getPX();

	/**
	 * Previous Mouse Y
	 */
	public int getPY();

	/**
	 * Previous Mouse Wheel
	 */
	public int getPW();

	/**
	 * Delta Mouse X
	 */
	public default int getDX()
	{
		return getX() - getPX();
	}

	/**
	 * Delta Mouse Y
	 */
	public default int getDY()
	{
		return getY() - getPY();
	}

	/**
	 * Delta Mouse Wheel
	 */
	public default int getDW()
	{
		return getW() - getPW();
	}

	public IButton getButton(int index);

	public IButton getKey(int index);

	public int getButtonCount();

	public int getKeyCount();

}
