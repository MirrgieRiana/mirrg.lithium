package mirrg.lithium.lang;

import static org.junit.Assert.*;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import org.junit.Test;

public class TestHOptional
{

	@Test
	public void test_parse()
	{
		assertEquals(OptionalInt.of(198), HOptional.parseInt("198"));
		assertEquals(OptionalInt.of(198), HOptional.parseInt("+198"));
		assertEquals(OptionalInt.of(-198), HOptional.parseInt("-198"));
		assertEquals(OptionalInt.of(Integer.parseInt("0198")), HOptional.parseInt("0198"));
		assertEquals(OptionalInt.of(198), HOptional.parseInt("0198", 10));
		assertEquals(OptionalInt.empty(), HOptional.parseInt("53264738926553252", 10));
		assertEquals(OptionalInt.empty(), HOptional.parseInt("-53264738926553252", 10));

		assertEquals(OptionalLong.of(198), HOptional.parseLong("198"));
		assertEquals(OptionalLong.of(198), HOptional.parseLong("+198"));
		assertEquals(OptionalLong.of(-198), HOptional.parseLong("-198"));
		assertEquals(OptionalLong.of(Integer.parseInt("0198")), HOptional.parseLong("0198"));
		assertEquals(OptionalLong.of(198), HOptional.parseLong("0198", 10));
		assertEquals(OptionalLong.of(53264738926553252L), HOptional.parseLong("53264738926553252", 10));
		assertEquals(OptionalLong.of(-53264738926553252L), HOptional.parseLong("-53264738926553252", 10));

		assertEquals(OptionalInt.empty(), HOptional.parseInt(" 198"));
		assertEquals(OptionalInt.empty(), HOptional.parseInt("198 "));
		assertEquals(OptionalInt.empty(), HOptional.parseInt("198i"));
		assertEquals(OptionalInt.empty(), HOptional.parseInt("198.0"));

		assertEquals(OptionalDouble.of(1.1), HOptional.parseDouble("1.1"));
		assertEquals(OptionalDouble.of(.1), HOptional.parseDouble(".1"));
		assertEquals(OptionalDouble.of(1.), HOptional.parseDouble("1."));
		assertEquals(OptionalDouble.of(0), HOptional.parseDouble("0"));
		assertEquals(OptionalDouble.of(-0.0), HOptional.parseDouble("-0.0"));
		assertEquals(OptionalDouble.of(-0.0), HOptional.parseDouble("  -0.0  "));

		assertTrue(HOptional.parseDouble("NaN").isPresent());
		assertTrue(HOptional.parseDouble("+NaN").isPresent());
		assertTrue(HOptional.parseDouble("-NaN").isPresent());
		assertTrue(HOptional.parseDouble("Infinity").isPresent());
		assertTrue(HOptional.parseDouble("+Infinity").isPresent());
		assertTrue(HOptional.parseDouble("-Infinity").isPresent());

		assertEquals(OptionalDouble.of(-1.5E4), HOptional.parseDouble("-1.5E4"));
		assertEquals(OptionalDouble.of(-1.5e4), HOptional.parseDouble("-1.5e4"));
		assertEquals(OptionalDouble.of(-1.5e-4), HOptional.parseDouble("-1.5e-4"));

		assertFalse(HOptional.parseDouble("").isPresent());
		assertFalse(HOptional.parseDouble("a").isPresent());
		assertFalse(HOptional.parseDouble("5,4").isPresent());
	}

}
