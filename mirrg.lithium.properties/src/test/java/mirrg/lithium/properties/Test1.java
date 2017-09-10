package mirrg.lithium.properties;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import net.arnx.jsonic.JSON;

public class Test1
{

	@Test
	public void test1() throws Exception
	{
		File file = new File("test.properties");
		IProperties properties = HPropertiesParser.parse(file, System.err::println);
		assertEquals("test-5.0.1.jar", properties.get("archiveName").getString().get());
		assertEquals(1, (int) properties.get("a").getInteger().get());
		assertEquals(2, (int) properties.get("b").getInteger().get());
		assertEquals("3", properties.get("c").getString().get());
		assertEquals(4, (int) properties.get("d.e").getInteger().get());
		assertEquals(5, (int) properties.get("f/g").getInteger().get());
		assertEquals("", properties.get("h").getString().get());
		assertEquals("", properties.get("i").getString().get());
		assertEquals("", properties.get("j").getString().get());
		assertEquals("", properties.get("k").getString().get());

		assertEquals(6, (int) properties.get("abc_-./:()01").getInteger().get());

		assertEquals("\"'\\345", properties.get("l").getString().get());
		assertEquals("\"'\\${10}", properties.get("m").getString().get());
		assertEquals("\\\\ \\ 345", properties.get("n").getString().get());

		assertEquals(new File("test.properties").getAbsoluteFile().getPath(), properties.get("o2").getString().get());
		assertEquals(new File("test.properties").getAbsoluteFile().getParent(), properties.get("p2").getString().get());
		assertEquals(new File("test2.properties").getAbsoluteFile().getPath(), properties.get("o").getString().get());
		assertEquals(new File("test2.properties").getAbsoluteFile().getParent(), properties.get("p").getString().get());

		assertEquals("200", properties.get("r1").getString().get());
		assertEquals("100", properties.get("r2").getString().get());
		assertEquals("100200", properties.get("s").getString().get());
		assertEquals("500", properties.get("u1").getString().get());
		assertEquals("200", properties.get("u2").getString().get());

		assertEquals(1.35, properties.get("double").getDouble().get(), 0.001);
		assertEquals(true, (boolean) properties.get("boolean1").getBoolean().get());
		assertEquals(false, (boolean) properties.get("boolean2").getBoolean().get());
	}

	@Test
	public void test3() throws Exception
	{
		File file = new File("test3.properties");
		IProperties properties = HPropertiesParser.parse(file, System.err::println);

		assertFalse(properties.get("a").getString().isPresent());
		assertEquals(1, (int) properties.get("a.a").getInteger().get());
		assertEquals(2, (int) properties.get("a.b").getInteger().get());
		assertEquals("{a:2,b:[4,5,6]}", properties.get("b").getString().get().replaceAll("\\s", ""));
		assertEquals("{a:${a.b},b:[4,5,6]}", properties.get("c").getString().get().replaceAll("\\s", ""));
		assertEquals("{a=2}", properties.get("d").getString().get().replaceAll("\\s", ""));
		assertEquals("{a=${a.b}}", properties.get("e").getString().get().replaceAll("\\s", ""));

		{
			String json = properties.get("f").getString().get();

			ClassA a = JSON.decode(json, ClassA.class);

			assertEquals(2, a.a.a);
			assertEquals(3, a.a.b.length);
			assertEquals(4, a.a.b[0]);
			assertEquals(5, a.a.b[1]);
			assertEquals(6, a.a.b[2]);
		}

		assertEquals(0, (int) properties.get("g.a").getInteger().get());
		assertEquals(1, (int) properties.get("g.a.a").getInteger().get());
		assertEquals(2, (int) properties.get("g.a.b").getInteger().get());
		assertEquals(3, (int) properties.get("g.b").getInteger().get());

		assertEquals("10", properties.get("i.a").getString().get().replaceAll("\\s", ""));
	}

	public static class ClassA
	{
		public ClassB a;
	}

	public static class ClassB
	{
		public int a;
		public int[] b;
	}

	@Test
	public void test_exception_message() throws FileNotFoundException
	{
		int[] count = new int[1];
		HPropertiesParser.parse(new File("test_exception.properties"), e -> {
			if (e instanceof SyntaxException) {
				assertEquals("Syntax error at test_exception.properties (R:2 C:4)\nasd\n   ^\nExpected: =, whitespace, {", e.getMessage());
			} else {
				fail();
			}
			count[0]++;
		});
		assertEquals(1, count[0]);
	}

}
