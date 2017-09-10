package mirrg.lithium.parser;

import static mirrg.lithium.parser.HSyntaxOxygen.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.junit.Test;

import mirrg.lithium.parser.core.ResultOxygen;
import mirrg.lithium.parser.core.Syntax;
import mirrg.lithium.parser.syntaxes.SyntaxOr;
import mirrg.lithium.parser.syntaxes.SyntaxSlot;
import mirrg.lithium.struct.Struct1;
import mirrg.lithium.struct.Struct2;

public class Test1
{

	public static final double D = 0.001;

	@Test
	public void test1()
	{
		Syntax<IFormula> syntaxFactor = pack(
			regex("\\d+"),
			s -> new FormulaLiteral(Integer.parseInt(s, 10)));
		Syntax<IFormula> syntaxExpression = wrap(serial(FormulaOperation::new)
			.and(syntaxFactor, FormulaOperation::setLeft)
			.and(string("+"), (f, f2) -> f.function = (a, b) -> a + b)
			.and(syntaxFactor, FormulaOperation::setRight));

		ToDoubleFunction<String> f = src -> syntaxExpression.parse(src).value.calculate();

		assertEquals(2, f.applyAsDouble("1+1"), D);
		assertEquals(133, f.applyAsDouble("010+123"), D);
		assertEquals(50, f.applyAsDouble("0045+5"), D);
		assertEquals(700, f.applyAsDouble("200+500"), D);
		assertEquals(0, f.applyAsDouble("0+0"), D);
	}

	@Test
	public void test2()
	{
		Hashtable<String, Double> constants = new Hashtable<>();
		constants.put("pi", Math.PI);
		constants.put("e", Math.E);

		Syntax<IFormula> syntaxInteger = pack(
			regex("\\d+"),
			s -> new FormulaLiteral(Integer.parseInt(s, 10)));
		Syntax<IFormula> syntaxConstant = pack(
			regex("[a-zA-Z_][a-zA-Z_0-9]*"),
			s -> new FormulaLiteral(constants.get(s)));
		SyntaxOr<IFormula> syntaxFactor = or((IFormula) null)
			.or(syntaxInteger)
			.or(syntaxConstant);
		Syntax<IFormula> syntaxExpression = wrap(serial(FormulaOperation::new)
			.and(syntaxFactor, FormulaOperation::setLeft)
			.and(string("+"), (f, f2) -> f.function = (a, b) -> a + b)
			.and(syntaxFactor, FormulaOperation::setRight));

		ToDoubleFunction<String> f = src -> syntaxExpression.parse(src).value.calculate();

		assertEquals(Math.PI, f.applyAsDouble("0+pi"), D);
	}

	public static Syntax<IFormula> operation2(
		Syntax<IFormula> syntaxOperand,
		Syntax<IFunction> syntaxOperator)
	{
		return wrap(serial(FormulaOperationArray::new)
			.and(syntaxOperand, (n1, n2) -> n1.left = n2)
			.and(repeat(serial(Struct2<IFunction, IFormula>::new)
				.and(syntaxOperator, (n1, n2) -> n1.x = n2)
				.and(syntaxOperand, (n1, n2) -> n1.y = n2)),
				(n1, n2) -> n1.right = n2));
	}

	public static Syntax<IFormula> test3_getSyntax()
	{
		Hashtable<String, Double> constants = new Hashtable<>();
		constants.put("pi", Math.PI);
		constants.put("e", Math.E);

		Syntax<IFormula> syntaxInteger = pack(named(regex("\\d+"), "Integer"),
			s -> new FormulaLiteral(Integer.parseInt(s, 10)));
		Syntax<IFormula> syntaxConstant = pack(named(regex("[a-zA-Z_][a-zA-Z_0-9]*"), "Constant"),
			s -> new FormulaVariable(s, constants));
		SyntaxSlot<IFormula> syntaxExpression = slot();
		Syntax<IFormula> syntaxBrackets = pack(serial(Struct1<IFormula>::new)
			.and(string("("))
			.and(syntaxExpression, Struct1::setX)
			.and(string(")")),
			Struct1::getX);
		SyntaxOr<IFormula> syntaxFactor = or((IFormula) null)
			.or(syntaxInteger)
			.or(syntaxConstant)
			.or(syntaxBrackets);
		Syntax<IFormula> syntaxTerm = wrap(operation2(
			syntaxFactor,
			or((IFunction) null)
				.or(pack(string("*"), s -> (a, b) -> a * b))
				.or(pack(string("/"), s -> (a, b) -> a / b))));
		syntaxExpression.setSyntax(wrap(operation2(
			syntaxTerm,
			or((IFunction) null)
				.or(pack(string("+"), s -> (a, b) -> a + b))
				.or(pack(string("-"), s -> (a, b) -> a - b)))));
		return syntaxExpression;
	}

	@Test
	public void test3()
	{
		Syntax<IFormula> syntaxExpression = test3_getSyntax();

		ToDoubleFunction<String> f = src -> syntaxExpression.parse(src).value.calculate();

		assertEquals(77.9852278869, f.applyAsDouble("15/26*158+41-27*14/7+45/61*5-27/7"), D);
		assertEquals(-3.85706255112, f.applyAsDouble("15/(26*158+41-27)*(14/(7+45)/61)*5-27/7"), D);
	}

	@Test
	public void test4()
	{
		Syntax<IFormula> syntaxInteger = pack(regex("\\d+"),
			s -> new FormulaLiteral(Integer.parseInt(s, 10)));
		Syntax<IFormula> syntaxPower = pack(operation(
			syntaxInteger,
			or((IFunction) null)
				.or(pack(string("^"), s -> (a, b) -> Math.pow(a, b)))),
			o -> o.calculateRight((left, operator, right) -> () -> operator.apply(left.calculate(), right.calculate())));
		Syntax<IFormula> syntaxTerm = pack(operation(
			syntaxPower,
			or((IFunction) null)
				.or(pack(string("*"), s -> (a, b) -> a * b))
				.or(pack(string("/"), s -> (a, b) -> a / b))),
			o -> o.calculateLeft((left, operator, right) -> () -> operator.apply(left.calculate(), right.calculate())));
		Syntax<IFormula> syntaxExpression = pack(operation(
			syntaxTerm,
			or((IFunction) null)
				.or(pack(string("+"), s -> (a, b) -> a + b))
				.or(pack(string("-"), s -> (a, b) -> a - b))),
			o -> o.calculateLeft((left, operator, right) -> () -> operator.apply(left.calculate(), right.calculate())));

		ToDoubleFunction<String> f = src -> syntaxExpression.parse(src).value.calculate();

		assertEquals(3, f.applyAsDouble("1+1+1"), D);
		assertEquals(7, f.applyAsDouble("1+2*3"), D);
		assertEquals(77.9852278869, f.applyAsDouble("15/26*158+41-27*14/7+45/61*5-27/7"), D);
		assertEquals(512, f.applyAsDouble("2^3^2"), D);
	}

	@Test
	public void testName()
	{
		Syntax<String> syntaxA = string("A");
		Syntax<Supplier<String>> syntaxB = named(tunnel((String) null)
			.extract(syntaxA)
			.and(string("B")), "B");

		ResultOxygen<Supplier<String>> result = syntaxB.matches("C");
		String[] tokens = result.getTokenProposal().stream()
			.map(s -> s.getName())
			.filter(n -> n != null)
			.toArray(String[]::new);

		assertEquals(0, result.getTokenProposalIndex());
		assertEquals(1, tokens.length);
		assertEquals("B", tokens[0]);
	}

	@Test
	public void testDeliter()
	{
		{
			Syntax<Integer> syntax = pack(serial(Struct2<String, ArrayList<String>>::new)
				.and(regex("."), Struct2::setX)
				.and(t -> repeat(string(t.x)), Struct2::setY),
				t -> 1 + t.y.size());

			ToIntFunction<String> f = src -> syntax.parse(src).value;

			assertEquals(3, f.applyAsInt("///"));
			assertEquals(7, f.applyAsInt("???????"));
			assertEquals(1, f.applyAsInt("a"));
		}
		{
			Syntax<String> syntax = pack(serial(Struct2<String, ArrayList<Supplier<String>>>::new)
				.and(regex("."), Struct2::setX)
				.and(t -> repeat(tunnel((String) null)
					.and(negative(string(t.x)))
					.extract(regex("."))), Struct2::setY)
				.and(t -> string(t.x)),
				t -> t.y.stream()
					.map(Supplier::get)
					.collect(Collectors.joining()));

			Function<String, String> f = src -> syntax.parse(src).value;

			assertEquals("abc", f.apply("/abc/"));
			assertEquals("", f.apply("??"));
			assertEquals("k", f.apply(".k."));
			assertEquals("io", f.apply("(io("));
		}
	}

}
