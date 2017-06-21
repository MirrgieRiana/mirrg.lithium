package mirrg.lithium.properties;

import static mirrg.lithium.parser.HSyntaxOxygen.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import mirrg.lithium.parser.core.Node;
import mirrg.lithium.parser.core.Syntax;
import mirrg.lithium.struct.Struct2;

public class HPropertiesParser
{

	public static Properties parse(File file, Consumer<Exception> onException) throws FileNotFoundException
	{
		return parse(new FileInputStream(file), file.getParentFile(), onException);
	}

	public static Properties parse(InputStream in, File currentDirectory, Consumer<Exception> onException)
	{
		return parse(new BufferedReader(new InputStreamReader(in)), currentDirectory, onException);
	}

	public static Properties parse(BufferedReader in, File currentDirectory, Consumer<Exception> onException)
	{
		return parse(in.lines(), currentDirectory, onException);
	}

	public static Properties parse(Stream<String> lines, File currentDirectory, Consumer<Exception> onException)
	{
		return parse(lines.toArray(String[]::new), currentDirectory, onException);
	}

	public static Properties parse(Collection<String> lines, File currentDirectory, Consumer<Exception> onException)
	{
		return parse(lines.stream(), currentDirectory, onException);
	}

	public static Properties parse(String string, File currentDirectory, Consumer<Exception> onException)
	{
		return parse(string.split("\r\n?|\n"), currentDirectory, onException);
	}

	public static Properties parse(String[] lines, File currentDirectory, Consumer<Exception> onException)
	{
		Properties properties = new Properties();
		PropertiesContext propertiesContext = new PropertiesContext(properties, currentDirectory);

		for (String line2 : lines) {
			Node<BiConsumer<PropertiesContext, Consumer<Exception>>> node = line.parse(line2);
			if (node == null) {
				onException.accept(new NumberFormatException(line2));
			} else {
				node.value.accept(propertiesContext, onException::accept);
			}
		}

		return properties;
	}

	//////////////////////////////////////////////////////////////////////////////

	private static Syntax<String> __ = regex("[ \\t]*");

	private static Syntax<String> propertyName = regex("[a-zA-Z0-9_\\-./:()]*");

	public static Syntax<String> inheritance = regex(".*");

	public static Syntax<BiConsumer<PropertiesContext, Consumer<Exception>>> lineInherits = pack(tunnel((String) null)
		.and(__)
		.and(string("@inherit"))
		.and(__)
		.and(string(":"))
		.and(__)
		.extract(inheritance),
		t -> (pc, c) -> {
			Properties child;
			try {
				child = parse(new File(pc.currentDirectory, t.get()), c);
			} catch (FileNotFoundException e) {
				c.accept(e);
				return;
			}
			pc.properties.addParent(child);
		});

	private static Syntax<IMethod> methodStaticPart = or((IMethod) null)
		.or(pack(tunnel((String) null)
			.and(string("\\"))
			.extract(regex(".")),
			t -> p -> t.get()))
		.or(pack(regex("[^\\']"),
			t -> p -> t));

	private static Syntax<ArrayList<IMethod>> methodStatic = pack(tunnel((ArrayList<IMethod>) null)
		.and(string("'"))
		.extract(repeat(methodStaticPart))
		.and(string("'")),
		t -> t.get());

	private static Syntax<IMethod> methodDynamicPart = or((IMethod) null)
		.or(pack(tunnel((String) null)
			.and(string("\\"))
			.extract(regex(".")),
			t -> p -> t.get()))
		.or(pack(tunnel((String) null)
			.and(string("${"))
			.and(__)
			.extract(propertyName)
			.and(__)
			.and(string("}")),
			t -> p -> p.getString(t.get()).orElse("")))
		.or(pack(regex("[^\\\"]"),
			t -> p -> t));

	private static Syntax<ArrayList<IMethod>> methodDynamic = pack(tunnel((ArrayList<IMethod>) null)
		.and(string("\""))
		.extract(repeat(methodDynamicPart))
		.and(string("\"")),
		t -> t.get());

	private static Syntax<IMethod> methodPlainPart = or((IMethod) null)
		.or(pack(tunnel((String) null)
			.and(string("${"))
			.extract(propertyName)
			.and(string("}")),
			t -> p -> p.getString(t.get()).orElse("")))
		.or(pack(regex("."),
			t -> p -> t));

	private static Syntax<ArrayList<IMethod>> methodPlain = repeat(methodPlainPart);

	public static Syntax<IMethod> method = pack(or((ArrayList<IMethod>) null)
		.or(methodStatic)
		.or(methodDynamic)
		.or(methodPlain),
		t -> p -> {
			StringBuilder sb = new StringBuilder();
			t.forEach(m -> sb.append(m.apply(p)));
			return sb.toString();
		});

	public static Syntax<BiConsumer<PropertiesContext, Consumer<Exception>>> lineMethod = pack(serial(Struct2<String, IMethod>::new)
		.and(__)
		.and(propertyName, Struct2::setX)
		.and(__)
		.and(string("="))
		.and(__)
		.and(method, Struct2::setY),
		t -> (pc, c) -> pc.properties.put(t.x, t.y));

	public static Syntax<BiConsumer<PropertiesContext, Consumer<Exception>>> lineComment = pack(regex("#.*"),
		t -> (pc, c) -> {});

	public static Syntax<BiConsumer<PropertiesContext, Consumer<Exception>>> lineBlank = pack(__,
		t -> (pc, c) -> {});

	public static Syntax<BiConsumer<PropertiesContext, Consumer<Exception>>> line = or((BiConsumer<PropertiesContext, Consumer<Exception>>) null)
		.or(lineInherits)
		.or(lineMethod)
		.or(lineComment)
		.or(lineBlank);

}
