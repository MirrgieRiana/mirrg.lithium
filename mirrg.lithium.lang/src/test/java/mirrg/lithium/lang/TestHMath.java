package mirrg.lithium.lang;

import static mirrg.lithium.lang.HMath.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestHMath
{

	@Test
	public void test_nice()
	{
		assertEquals(50, nice(56.5), 0.01);
		assertEquals(50, nice(50.5), 0.01);
		assertEquals(20, nice(49.9), 0.01);
		assertEquals(20, nice(20.1), 0.01);
		assertEquals(10, nice(19.9), 0.01);
		assertEquals(10, nice(10.1), 0.01);
		assertEquals(5, nice(9.99), 0.001);
	}

}
