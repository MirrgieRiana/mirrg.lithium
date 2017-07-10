package mirrg.lithium.struct;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

public class TestImmutableArray
{

	@Test
	public void test1()
	{
		ImmutableArray<String> array1;
		{
			array1 = new ImmutableArray<>("a", "b");
			assertEquals(2, array1.length());
			assertEquals("a", array1.get(0));
			assertEquals("b", array1.get(1));
		}

		ImmutableArray<String> array2;
		{
			String[] array = {
				"a", "b",
			};
			array2 = new ImmutableArray<>(array);
			array[0] = "c";
			array[1] = "d";
			assertEquals(2, array2.length());
			assertEquals("a", array2.get(0));
			assertEquals("b", array2.get(1));
		}

		ImmutableArray<String> array3;
		{
			ArrayList<String> array = new ArrayList<String>() {
				{
					add("a");
					add("b");
				}
			};
			array3 = new ImmutableArray<>(array);
			array.set(0, "c");
			array.set(1, "d");
			assertEquals(2, array3.length());
			assertEquals("a", array3.get(0));
			assertEquals("b", array3.get(1));
		}

		assertEquals(array1, array2);
		assertEquals(array2, array3);
		assertEquals(array1, array3);

		ImmutableArray<String> array4 = new ImmutableArray<>("a", "c");
		assertNotEquals(array1, array4);
		assertNotEquals(array2, array4);
		assertNotEquals(array3, array4);
		assertNotEquals(array4, array1);
		assertNotEquals(array4, array2);
		assertNotEquals(array4, array3);

		ImmutableArray<String> array5 = new ImmutableArray<>("a");
		assertNotEquals(array1, array5);
		assertNotEquals(array2, array5);
		assertNotEquals(array3, array5);
		assertNotEquals(array4, array5);
		assertNotEquals(array5, array1);
		assertNotEquals(array5, array2);
		assertNotEquals(array5, array3);
		assertNotEquals(array5, array4);

		ImmutableArray<String> array6 = new ImmutableArray<>("a", "b", "c");
		assertNotEquals(array1, array6);
		assertNotEquals(array2, array6);
		assertNotEquals(array3, array6);
		assertNotEquals(array4, array6);
		assertNotEquals(array5, array6);
		assertNotEquals(array6, array1);
		assertNotEquals(array6, array2);
		assertNotEquals(array6, array3);
		assertNotEquals(array6, array4);
		assertNotEquals(array6, array5);

		ArrayList<String> list1 = Collections.list(array6.values());
		assertEquals(array6.length(), list1.size());
		assertEquals(array6.get(0), list1.get(0));
		assertEquals(array6.get(1), list1.get(1));
		assertEquals(array6.get(2), list1.get(2));

		String[] list2 = array6.stream().toArray(String[]::new);
		assertEquals(array6.length(), list2.length);
		assertEquals(array6.get(0), list2[0]);
		assertEquals(array6.get(1), list2[1]);
		assertEquals(array6.get(2), list2[2]);

	}

}
