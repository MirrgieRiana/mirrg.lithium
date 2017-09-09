package mirrg.lithium.parser.syntaxes;

import mirrg.lithium.struct.ImmutableArray;

public class Operation<OPERAND, OPERATOR>
{

	public final ImmutableArray<OPERAND> operands;
	public final ImmutableArray<OPERATOR> operators;

	public Operation(ImmutableArray<OPERAND> operands, ImmutableArray<OPERATOR> operators)
	{
		this.operands = operands;
		this.operators = operators;
	}

	public OPERAND calculateLeft(ICalculator<OPERAND, OPERATOR> calculator)
	{
		OPERAND left = operands.get(0);
		for (int i = 0; i < operators.length(); i++) {
			left = calculator.calculate(left, operators.get(i), operands.get(1 + i));
		}
		return left;
	}

	public OPERAND calculateRight(ICalculator<OPERAND, OPERATOR> calculator)
	{
		OPERAND right = operands.get(operators.length());
		for (int i = operators.length() - 1; i >= 0; i--) {
			right = calculator.calculate(operands.get(i), operators.get(i), right);
		}
		return right;
	}

	public static interface ICalculator<OPERAND, OPERATOR>
	{

		public OPERAND calculate(OPERAND left, OPERATOR operator, OPERAND right);

	}

}
