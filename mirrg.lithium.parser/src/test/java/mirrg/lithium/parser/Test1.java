package mirrg.lithium.parser;

import static mirrg.lithium.parser.HSyntaxOxygen.*;
import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.function.ToDoubleFunction;

import org.junit.Test;

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

	public static Syntax<IFormula> operation(
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
		Syntax<IFormula> syntaxTerm = wrap(operation(
			syntaxFactor,
			or((IFunction) null)
				.or(pack(string("*"), s -> (a, b) -> a * b))
				.or(pack(string("/"), s -> (a, b) -> a / b))));
		syntaxExpression.setSyntax(wrap(operation(
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

}
