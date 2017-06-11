package mirrg.helium.standard.hydrogen.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputStatus implements IInputStatus, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{

	private int x;
	private int y;
	private int w;
	private int px;
	private int py;
	private int pw;

	private Button[] buttons;
	private Button[] keys;

	public InputStatus()
	{
		this(8, 1024);
	}

	public InputStatus(int buttonsCount, int keysCount)
	{
		buttons = new Button[buttonsCount];
		keys = new Button[keysCount];

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new Button();
		}
		for (int i = 0; i < keys.length; i++) {
			keys[i] = new Button();
		}
	}

	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public int getY()
	{
		return y;
	}

	@Override
	public int getW()
	{
		return w;
	}

	@Override
	public int getPX()
	{
		return px;
	}

	@Override
	public int getPY()
	{
		return py;
	}

	@Override
	public int getPW()
	{
		return pw;
	}

	@Override
	public Button getButton(int index)
	{
		return buttons[index];
	}

	@Override
	public Button getKey(int index)
	{
		return keys[index];
	}

	@Override
	public int getButtonCount()
	{
		return buttons.length;
	}

	@Override
	public int getKeyCount()
	{
		return keys.length;
	}

	public synchronized void update()
	{
		px = x;
		py = y;
		pw = w;

		for (Button button : buttons) {
			button.update();
		}
		for (Button key : keys) {
			key.update();
		}
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		mousePressed(event.getX(), event.getY(), event.getButton());
	}

	public synchronized void mousePressed(int x, int y, int button)
	{
		this.x = x;
		this.y = y;
		buttons[button].press();
	}

	public synchronized void mousePressed(int button)
	{
		buttons[button].press();
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		mouseReleased(event.getX(), event.getY(), event.getButton());
	}

	public synchronized void mouseReleased(int x, int y, int button)
	{
		this.x = x;
		this.y = y;
		buttons[button].release();
	}

	public synchronized void mouseReleased(int button)
	{
		buttons[button].release();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mouseDragged(MouseEvent event)
	{
		mouseDragged(event.getX(), event.getY());
	}

	public synchronized void mouseDragged(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public void mouseMoved(MouseEvent event)
	{
		mouseMoved(event.getX(), event.getY());
	}

	public synchronized void mouseMoved(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{
		mouseWheelMoved(event.getWheelRotation());
	}

	public synchronized void mouseWheelMoved(int wheelRotation)
	{
		w += wheelRotation;
	}

	@Override
	public void keyPressed(KeyEvent event)
	{
		keyPressed(event.getKeyCode());
	}

	public synchronized void keyPressed(int keyCode)
	{
		keys[keyCode].press();
	}

	@Override
	public void keyReleased(KeyEvent event)
	{
		keyReleased(event.getKeyCode());
	}

	public synchronized void keyReleased(int keyCode)
	{
		keys[keyCode].release();
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

}
