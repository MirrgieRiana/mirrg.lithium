package mirrg.lithium.parser;

import java.util.Hashtable;

public class FormulaVariable implements IFormula
{

	public String name;
	public Hashtable<String, Double> table;

	public FormulaVariable(String name, Hashtable<String, Double> table)
	{
		this.name = name;
		this.table = table;
	}

	@Override
	public double calculate()
	{
		return table.get(name);
	}

}
