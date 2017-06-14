package mirrg.lithium.parser;

public class FormulaOperation implements IFormula
{

	public IFormula left;
	public IFunction function;
	public IFormula right;

	@Override
	public double calculate()
	{
		return left.calculate() + right.calculate();
	}

	public void setLeft(IFormula left)
	{
		this.left = left;
	}

	public void setFunction(IFunction function)
	{
		this.function = function;
	}

	public void setRight(IFormula right)
	{
		this.right = right;
	}

}
