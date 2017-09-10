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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.lithium.lang.HString;
import mirrg.lithium.parser.core.ResultOxygen;
import mirrg.lithium.parser.core.Syntax;
import mirrg.lithium.parser.syntaxes.SyntaxSlot;
import mirrg.lithium.struct.Struct2;

public class HPropertiesParser
{

	public static IProperties parse(File file, Consumer<Exception> onException) throws FileNotFoundException
	{
		return parse(new FileInputStream(file), new PropertiesSource(file), onException);
	}

	public static IProperties parse(InputStream in, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		return parse(new BufferedReader(new InputStreamReader(in)), propertiesSource, onException);
	}

	public static IProperties parse(BufferedReader in, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		return parse(in.lines(), propertiesSource, onException);
	}

	public static IProperties parse(Collection<String> lines, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		return parse(lines.stream(), propertiesSource, onException);
	}

	public static IProperties parse(Stream<String> lines, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		return parse(lines.collect(Collectors.joining("\n")), propertiesSource, onException);
	}

	public static IProperties parse(String[] lines, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		return parse(String.join("\n", lines), propertiesSource, onException);
	}

	public static IProperties parse(String source, PropertiesSource propertiesSource, Consumer<Exception> onException)
	{
		PropertiesMultipleInheritable properties = new PropertiesMultipleInheritable();

		ResultOxygen<Consumer<VM>> result = rootLines.matches(source);
		VM vm = new VM(properties, propertiesSource, onException, source, result);
		if (result.isValid) {
			result.node.value.accept(vm);
		} else {
			onException.accept(new SyntaxException(vm, result));
		}

		return properties;
	}

	//////////////////////////////////////////////////////////////////////////////

	public static SyntaxSlot<Consumer<VM>> innerLines = slot();
	public static SyntaxSlot<Consumer<VM>> rootLines = slot();

	//

	private static Syntax<String> __ = named(regex("[ \\t]*"), "whitespace");

	private static Syntax<String> propertyName = named(regex("[a-zA-Z0-9_\\-./:()]+"), "propertyName");

	public static Syntax<String> inheritance = named(regex("[^\\r\\n]*"), "inheritance");

	public static Syntax<Consumer<VM>> lineInherits;
	static {
		lineInherits = pack(tunnel((String) null)
			.and(__)
			.and(string("@inherit"))
			.and(__)
			.and(string(":"))
			.and(__)
			.extract(inheritance),
			t -> vm -> {
				IProperties parent;
				try {
					parent = parse(new File(vm.propertiesSource.directory, t.get()), vm.onException);
				} catch (FileNotFoundException e) {
					vm.onException.accept(e);
					return;
				}
				vm.properties.addParent(parent);
			});
	}

	private static Syntax<Function<VM, IMethod>> methodStaticPart = named(or((Function<VM, IMethod>) null)
		.or(pack(tunnel((String) null)
			.and(string("\\"))
			.extract(regex(".")),
			t -> vm -> p -> new PropertyBasic(t.get())))
		.or(pack(regex("[^\\']"),
			t -> vm -> p -> new PropertyBasic(t))), "methodStaticPart");

	private static Syntax<ArrayList<Function<VM, IMethod>>> methodStatic = pack(tunnel(
		(ArrayList<Function<VM, IMethod>>) null)
			.and(string("'"))
			.extract(repeat(methodStaticPart))
			.and(string("'")),
		t -> t.get());

	private static SyntaxSlot<Function<VM, IMethod>> methodPartConstant = slot();
	static {
		methodPartConstant.syntax = or((Function<VM, IMethod>) null)
			.or(pack(tunnel((Function<VM, IMethod>) null)
				.and(string("${"))
				.and(__)
				.extract(or((Function<VM, IMethod>) null)
					.or(pack(propertyName,
						t -> vm -> p -> p.get(t)))
					.or(packNode(string("@file"),
						n -> vm -> {
							if (vm.propertiesSource.oFile.isPresent()) {
								return p -> new PropertyBasic(vm.propertiesSource.oFile.get().getAbsolutePath());
							} else {
								vm.onException.accept(new IllegalConstantAccessSyntaxException(vm, n.begin));
								return p -> new PropertyBasic("");
							}
						}))
					.or(pack(string("@dir"),
						t -> vm -> p -> new PropertyBasic(vm.propertiesSource.directory.getAbsolutePath())))
					.or(pack(tunnel((String) null)
						.and(string("@parent"))
						.and(__)
						.and(string(":"))
						.and(__)
						.extract(propertyName),
						t -> vm -> p -> vm.properties.getOfParents(t.get())))
					.or(pack(tunnel((String) null)
						.and(string("@current"))
						.and(__)
						.and(string(":"))
						.and(__)
						.extract(propertyName),
						t -> vm -> p -> vm.properties.get(t.get()))))
				.and(__)
				.and(string("}")),
				t -> t.get()))
			.or(pack(serial(Struct2<String, ArrayList<Supplier<Function<VM, IMethod>>>>::new)
				.and(string("$"))
				.and(pack(regex("\\[+"),
					s -> "'" + HString.rept(']', s.length())), Struct2::setX)
				.and(string("'"))
				.and(t -> repeat(tunnel((Function<VM, IMethod>) null)
					.and(negative(string(t.x)))
					.extract(pack(regex("[\\s\\S]"),
						s -> vm -> p -> new PropertyBasic(s)))), Struct2::setY)
				.and(t -> string(t.x)),
				t -> vm -> {
					ArrayList<IMethod> t2 = new ArrayList<>(); // 負荷対策
					t.y.forEach(sFM -> t2.add(sFM.get().apply(vm)));
					return p -> {
						StringBuilder sb = new StringBuilder();
						t2.forEach(m -> sb.append(m.apply(p)));
						return new PropertyBasic(sb.toString());
					};
				}))
			.or(pack(serial(Struct2<String, ArrayList<Supplier<Function<VM, IMethod>>>>::new)
				.and(string("$"))
				.and(pack(regex("\\[+"),
					s -> "\"" + HString.rept(']', s.length())), Struct2::setX)
				.and(string("\""))
				.and(t -> repeat(tunnel((Function<VM, IMethod>) null)
					.and(negative(string(t.x)))
					.extract(or((Function<VM, IMethod>) null)
						.or(methodPartConstant)
						.or(pack(regex("[\\s\\S]"),
							s -> vm -> p -> new PropertyBasic(s))))), Struct2::setY)
				.and(t -> string(t.x)),
				t -> vm -> {
					ArrayList<IMethod> t2 = new ArrayList<>(); // 負荷対策
					t.y.forEach(sFM -> t2.add(sFM.get().apply(vm)));
					return p -> {
						StringBuilder sb = new StringBuilder();
						t2.forEach(m -> sb.append(m.apply(p)));
						return new PropertyBasic(sb.toString());
					};
				}));
	}

	private static Syntax<Function<VM, IMethod>> methodDynamicPart = named(or((Function<VM, IMethod>) null)
		.or(pack(tunnel((String) null)
			.and(string("\\"))
			.extract(regex(".")),
			t -> vm -> p -> new PropertyBasic(t.get())))
		.or(methodPartConstant)
		.or(pack(regex("[^\\\"]"),
			t -> vm -> p -> new PropertyBasic(t))), "methodDynamicPart");

	private static Syntax<ArrayList<Function<VM, IMethod>>> methodDynamic = pack(tunnel(
		(ArrayList<Function<VM, IMethod>>) null)
			.and(string("\""))
			.extract(repeat(methodDynamicPart))
			.and(string("\"")),
		t -> t.get());

	private static Syntax<Function<VM, IMethod>> methodPlainPart = named(or((Function<VM, IMethod>) null)
		.or(methodPartConstant)
		.or(pack(regex("."),
			t -> vm -> p -> new PropertyBasic(t))), "methodPlainPart");

	private static Syntax<ArrayList<Function<VM, IMethod>>> methodPlain = repeat(methodPlainPart);

	public static Syntax<Function<VM, IMethod>> method = pack(or((ArrayList<Function<VM, IMethod>>) null)
		.or(methodStatic)
		.or(methodDynamic)
		.or(methodPlain),
		t -> vm -> {
			ArrayList<IMethod> t2 = new ArrayList<>(); // 負荷対策
			t.forEach(fM -> t2.add(fM.apply(vm)));
			return p -> {
				StringBuilder sb = new StringBuilder();
				t2.forEach(m -> sb.append(m.apply(p)));
				return new PropertyBasic(sb.toString());
			};
		});

	public static Syntax<Consumer<VM>> lineMethod = pack(serial(Struct2<String, Function<VM, IMethod>>::new)
		.and(__)
		.and(propertyName, Struct2::setX)
		.and(__)
		.and(string("="))
		.and(__)
		.and(method, Struct2::setY),
		t -> vm -> vm.putProperty(t.x, t.y.apply(vm)));

	public static Syntax<Consumer<VM>> lineGroup = or((Consumer<VM>) null)
		.or(pack(serial(Struct2<String, Consumer<VM>>::new)
			.and(__)
			.and(propertyName, Struct2::setX)
			.and(__)
			.and(string("{"))
			.and(__)
			.and(named(string("\n"), "linebreak"))
			.and(innerLines, Struct2::setY)
			.and(named(string("\n"), "linebreak"))
			.and(__)
			.and(string("}"))
			.and(__),
			t -> vm -> {
				String prefix = vm.prefix;
				vm.prefix += t.x + ".";
				t.y.accept(vm);
				vm.prefix = prefix;
			}))
		.or(pack(serial(() -> null)
			.and(__)
			.and(propertyName)
			.and(__)
			.and(string("{"))
			.and(__)
			.and(named(string("\n"), "linebreak"))
			.and(__)
			.and(string("}"))
			.and(__),
			t -> vm -> {}));

	public static Syntax<Consumer<VM>> lineComment = pack(serial(() -> null)
		.and(__)
		.and(named(regex("#[^\\r\\n]*"), "comment")),
		t -> vm -> {});

	public static Syntax<Consumer<VM>> lineBlank = pack(serial(() -> null)
		.and(__)
		.and(named(regex("(?=[\r\n])|$"), "beforeLinebreak")),
		t -> vm -> {});

	public static Syntax<Consumer<VM>> innerLine = or((Consumer<VM>) null)
		.or(lineMethod)
		.or(lineGroup)
		.or(lineComment)
		.or(lineBlank);

	static {
		innerLines.syntax = pack(operation(
			innerLine,
			named(string("\n"), "linebreak")),
			o -> vm -> o.operands.forEach(operand -> operand.accept(vm)));
	}

	public static Syntax<Consumer<VM>> rootLine = or((Consumer<VM>) null)
		.or(lineInherits)
		.or(lineMethod)
		.or(lineGroup)
		.or(lineComment)
		.or(lineBlank);

	static {
		rootLines.syntax = pack(operation(
			rootLine,
			named(string("\n"), "linebreak")),
			o -> vm -> o.operands.forEach(operand -> operand.accept(vm)));
	}

}
