package mirrg.lithium.properties;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

public class Test1
{

	@Test
	public void test1() throws Exception
	{
		File file = new File("test.properties");
		Properties properties = HPropertiesParser.parse(file, System.err::println);
		assertEquals("test-5.0.1.jar", properties.getString("archiveName").get());
		assertEquals(1, (int) properties.getInteger("a").get());
		assertEquals(2, (int) properties.getInteger("b").get());
		assertEquals("3", properties.getString("c").get());
		assertEquals(4, (int) properties.getInteger("d.e").get());
		assertEquals(5, (int) properties.getInteger("f/g").get());
		assertEquals("", properties.getString("h").get());
		assertEquals("", properties.getString("i").get());
		assertEquals("", properties.getString("j").get());
		assertEquals("", properties.getString("k").get());

		assertEquals(6, (int) properties.getInteger("abc_-./:()01").get());

		assertEquals("\"'\\345", properties.getString("l").get());
		assertEquals("\"'\\${10}", properties.getString("m").get());
		assertEquals("\\\\ \\ 345", properties.getString("n").get());

		assertEquals(new File("test.properties").getAbsoluteFile().getPath(), properties.getString("o2").get());
		assertEquals(new File("test.properties").getAbsoluteFile().getParent(), properties.getString("p2").get());
		assertEquals(new File("test2.properties").getAbsoluteFile().getPath(), properties.getString("o").get());
		assertEquals(new File("test2.properties").getAbsoluteFile().getParent(), properties.getString("p").get());

		assertEquals("200", properties.getString("r1").get());
		assertEquals("100", properties.getString("r2").get());
		assertEquals("100200", properties.getString("s").get());

		assertEquals(1.35, properties.getDouble("double").get(), 0.001);
		assertEquals(true, (boolean) properties.getBoolean("boolean1").get());
		assertEquals(false, (boolean) properties.getBoolean("boolean2").get());
	}

	@Test
	public void test_exception_message() throws FileNotFoundException
	{
		int[] count = new int[1];
		Properties properties = HPropertiesParser.parse(new File("test_exception.properties"), e -> {
			if (e instanceof SyntaxException) {
				assertEquals("Syntax error at test_exception.properties (R:2 C:4)\nasd\n   ^", e.getMessage());
			} else {
				fail();
			}
			count[0]++;
		});
		assertEquals(1, count[0]);
	}

}
