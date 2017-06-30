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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import mirrg.lithium.parser.core.ResultOxygen;
import mirrg.lithium.parser.core.Syntax;
import mirrg.lithium.struct.Struct2;

public class HPropertiesParser
{

	public static Properties parse(File file, Consumer<Exception> onException) throws FileNotFoundException
	{
		return parse(new FileInputStream(file), new PropertiesSource(file), onException);
	}

	public static Properties parse(InputStream in, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		return parse(new BufferedReader(new InputStreamReader(in)), propertiesSource, onException);
	}

	public static Properties parse(BufferedReader in, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		return parse(in.lines(), propertiesSource, onException);
	}

	public static Properties parse(Stream<String> lines, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		return parse(lines.toArray(String[]::new), propertiesSource, onException);
	}

	public static Properties parse(Collection<String> lines, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		return parse(lines.stream(), propertiesSource, onException);
	}

	public static Properties parse(String string, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		return parse(string.split("\r\n?|\n"), propertiesSource, onException);
	}

	public static Properties parse(String[] lines, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		Properties properties = new Properties();

		for (int i = 0; i < lines.length; i++) {
			ResultOxygen<Consumer<VM>> result = line.matches(lines[i]);
			VM vm = new VM(properties, propertiesSource, onException, lines[i], i + 1, result);
			if (result.isValid) {
				result.node.value.accept(vm);
			} else {
				onException.accept(new SyntaxException(vm, result.getTokenProposalIndex() + 1));
			}
		}

		return properties;
	}

	//////////////////////////////////////////////////////////////////////////////

	private static Syntax<String> __ = regex("[ \\t]*");

	private static Syntax<String> propertyName = regex("[a-zA-Z0-9_\\-./:()]+");

	public static Syntax<String> inheritance = regex(".*");

	public static Syntax<Consumer<VM>> lineInherits = pack(tunnel((String) null)
		.and(__)
		.and(string("@inherit"))
		.and(__)
		.and(string(":"))
		.and(__)
		.extract(inheritance),
		t -> vm -> {
			Properties child;
			try {
				child = parse(new File(vm.propertiesSource.directory, t.get()), vm.onException);
			} catch (FileNotFoundException e) {
				vm.onException.accept(e);
				return;
			}
			vm.properties.addParent(child);
		});

	private static Syntax<Function<VM, IMethod>> methodStaticPart = or((Function<VM, IMethod>) null)
		.or(pack(tunnel((String) null)
			.and(string("\\"))
			.extract(regex(".")),
			t -> vm -> p -> t.get()))
		.or(pack(regex("[^\\']"),
			t -> vm -> p -> t));

	private static Syntax<ArrayList<Function<VM, IMethod>>> methodStatic = pack(tunnel(
		(ArrayList<Function<VM, IMethod>>) null)
			.and(string("'"))
			.extract(repeat(methodStaticPart))
			.and(string("'")),
		t -> t.get());

	private static Syntax<Function<VM, IMethod>> methodPartConstant = pack(tunnel((Function<VM, IMethod>) null)
		.and(string("${"))
		.and(__)
		.extract(or((Function<VM, IMethod>) null)
			.or(pack(propertyName,
				t -> vm -> p -> p.getString(t).orElse("")))
			.or(packNode(string("@file"),
				n -> vm -> {
					if (vm.propertiesSource.oFile.isPresent()) {
						return p -> vm.propertiesSource.oFile.get().getAbsolutePath();
					} else {
						vm.onException.accept(new IllegalConstantAccessSyntaxException(vm, n.begin + 1));
						return p -> "";
					}
				}))
			.or(pack(string("@dir"),
				t -> vm -> p -> vm.propertiesSource.directory.getAbsolutePath())))
		.and(__)
		.and(string("}")),
		t -> t.get());

	private static Syntax<Function<VM, IMethod>> methodDynamicPart = or((Function<VM, IMethod>) null)
		.or(pack(tunnel((String) null)
			.and(string("\\"))
			.extract(regex(".")),
			t -> vm -> p -> t.get()))
		.or(methodPartConstant)
		.or(pack(regex("[^\\\"]"),
			t -> vm -> p -> t));

	private static Syntax<ArrayList<Function<VM, IMethod>>> methodDynamic = pack(tunnel(
		(ArrayList<Function<VM, IMethod>>) null)
			.and(string("\""))
			.extract(repeat(methodDynamicPart))
			.and(string("\"")),
		t -> t.get());

	private static Syntax<Function<VM, IMethod>> methodPlainPart = or((Function<VM, IMethod>) null)
		.or(methodPartConstant)
		.or(pack(regex("."),
			t -> vm -> p -> t));

	private static Syntax<ArrayList<Function<VM, IMethod>>> methodPlain = repeat(methodPlainPart);

	public static Syntax<Function<VM, IMethod>> method = pack(or((ArrayList<Function<VM, IMethod>>) null)
		.or(methodStatic)
		.or(methodDynamic)
		.or(methodPlain),
		t -> vm -> {
			ArrayList<IMethod> t2 = new ArrayList<>();
			t.forEach(fM -> t2.add(fM.apply(vm)));
			return p -> {
				StringBuilder sb = new StringBuilder();
				t2.forEach(m -> sb.append(m.apply(p)));
				return sb.toString();
			};
		});

	public static Syntax<Consumer<VM>> lineMethod = pack(serial(Struct2<String, Function<VM, IMethod>>::new)
		.and(__)
		.and(propertyName, Struct2::setX)
		.and(__)
		.and(string("="))
		.and(__)
		.and(method, Struct2::setY),
		t -> vm -> vm.properties.put(t.x, t.y.apply(vm)));

	public static Syntax<Consumer<VM>> lineComment = pack(regex("#.*"),
		t -> vm -> {});

	public static Syntax<Consumer<VM>> lineBlank = pack(__,
		t -> vm -> {});

	public static Syntax<Consumer<VM>> line = or((Consumer<VM>) null)
		.or(lineInherits)
		.or(lineMethod)
		.or(lineComment)
		.or(lineBlank);

}
