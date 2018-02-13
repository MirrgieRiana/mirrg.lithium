package mirrg.lithium.groovy.properties;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestGroovyProperties
{

	@Test
	public void test1() throws Exception
	{
		TestFunction testFunction;

		testFunction = TestFunctionFactory.createTestFunction(
			"assets://property1.groovy",
			5);
		assertEquals(5004, testFunction.get(4));

		testFunction = TestFunctionFactory.createTestFunction(
			"assets://property2.groovy",
			5);
		assertEquals(85234, testFunction.get(4));

	}

}
