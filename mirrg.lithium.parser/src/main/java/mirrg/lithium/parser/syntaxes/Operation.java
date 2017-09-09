package mirrg.lithium.parser.syntaxes;

import mirrg.lithium.struct.ImmutableArray;

public class Operation<OPERAND, OPERATOR>
{

	private ImmutableArray<OPERAND> operands;
	private ImmutableArray<OPERATOR> operators;

	public Operation(ImmutableArray<OPERAND> operands, ImmutableArray<OPERATOR> operators)
	{
		this.operands = operands;
		this.operators = operators;
	}

	public OPERAND getOperand(int index)
	{
		return operands.get(index);
	}

	public OPERATOR getOperator(int index)
	{
		return operators.get(index);
	}

	public int getOperandCount()
	{
		return operands.length();
	}

	public int getOperatorCount()
	{
		return operators.length();
	}

	public OPERAND calculateLeft(ICalculator<OPERAND, OPERATOR> calculator)
	{
		OPERAND left = getOperand(0);
		for (int i = 0; i < getOperatorCount(); i++) {
			left = calculator.calculate(left, getOperator(i), getOperand(1 + i));
		}
		return left;
	}

	public OPERAND calculateRight(ICalculator<OPERAND, OPERATOR> calculator)
	{
		OPERAND right = getOperand(getOperatorCount());
		for (int i = getOperatorCount() - 1; i >= 0; i--) {
			right = calculator.calculate(getOperand(i), getOperator(i), right);
		}
		return right;
	}

	public static interface ICalculator<OPERAND, OPERATOR>
	{

		public OPERAND calculate(OPERAND left, OPERATOR operator, OPERAND right);

	}

}
