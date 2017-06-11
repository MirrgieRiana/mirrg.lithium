package mirrg.helium.standard.hydrogen.input;

public class Button implements IButton
{

	private int state = 0;

	public void press()
	{
		if (state <= 0) state = 1;
	}

	public void release()
	{
		if (state >= 0) state = -1;
	}

	@Override
	public int getState()
	{
		return state;
	}

	public void update()
	{
		if (state > 0) {
			state++;
		} else if (state < 0) {
			state--;
		}
	}

}
