package mirrg.helium.standard.hydrogen.input;

public class InputStatusAdapter implements IInputStatus
{

	private IInputStatus inputStatus;

	public InputStatusAdapter(IInputStatus inputStatus)
	{
		this.inputStatus = inputStatus;
	}

	@Override
	public int getX()
	{
		return inputStatus.getX();
	}

	@Override
	public int getY()
	{
		return inputStatus.getY();
	}

	@Override
	public int getW()
	{
		return inputStatus.getW();
	}

	@Override
	public int getPX()
	{
		return inputStatus.getPX();
	}

	@Override
	public int getPY()
	{
		return inputStatus.getPY();
	}

	@Override
	public int getPW()
	{
		return inputStatus.getPW();
	}

	@Override
	public IButton getButton(int index)
	{
		return inputStatus.getButton(index);
	}

	@Override
	public IButton getKey(int index)
	{
		return inputStatus.getKey(index);
	}

	@Override
	public int getButtonCount()
	{
		return inputStatus.getButtonCount();
	}

	@Override
	public int getKeyCount()
	{
		return inputStatus.getKeyCount();
	}

}
