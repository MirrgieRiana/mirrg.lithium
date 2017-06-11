package mirrg.lithium.lang;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HLambda
{

	public static <T> T process(T object, Consumer<T> consumer)
	{
		consumer.accept(object);
		return object;
	}

	public static <T> T get(Supplier<T> supplier)
	{
		return supplier.get();
	}

	/**
	 * 添え字番号付きfor
	 */
	public static <T> void forEach(Stream<T> stream, ObjIntConsumer<T> consumer)
	{
		Integer[] i = new Integer[] {
			0,
		};
		stream.sequential().forEach(object -> {
			consumer.accept(object, i[0]);
			i[0]++;
		});
	}

	public static <T> Stream<T> toStream(Enumeration<T> e)
	{
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(
				new Iterator<T>() {

					@Override
					public T next()
					{
						return e.nextElement();
					}

					@Override
					public boolean hasNext()
					{
						return e.hasMoreElements();
					}

				},
				Spliterator.ORDERED), false);
	}

	public static <T> Stream<T> reverse(Stream<T> stream)
	{
		ArrayList<T> list = stream
			.collect(Collectors.toCollection(ArrayList::new));
		return rangeReverse(0, list.size())
			.mapToObj(list::get);
	}

	public static IntStream rangeReverse(int min, int max)
	{
		return IntStream.range(min, max)
			.map(i -> max - i + min - 1);
	}

	public static <I, O> Stream<O> filter(Stream<I> stream, Class<O> clazz)
	{
		return stream
			.filter(clazz::isInstance)
			.map(clazz::cast);
	}

}
