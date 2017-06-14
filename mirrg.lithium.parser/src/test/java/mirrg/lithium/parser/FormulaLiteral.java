package mirrg.lithium.parser;

public class FormulaLiteral implements IFormula
{

	public double value;

	public FormulaLiteral(double value)
	{
		this.value = value;
	}

	@Override
	public double calculate()
	{
		return value;
	}

}
