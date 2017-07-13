package mirrg.lithium.properties;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;

public class PropertiesMultipleInheritable implements IProperties
{

	private ArrayList<IProperties> parents = new ArrayList<>();
	private Hashtable<String, IMethod> methods = new Hashtable<>();

	public void addParent(IProperties parent)
	{
		parents.add(parent);
	}

	public void put(String key, IMethod method)
	{
		methods.put(key, method);
	}

	public void put(String key, String value)
	{
		methods.put(key, p -> new PropertyBasic(value));
	}

	@Override
	public Optional<IMethod> getMethod(String key)
	{
		IMethod method = methods.get(key);
		if (method != null) return Optional.of(method);
		return getMethodOfParents(key);
	}

	public Optional<IMethod> getMethodOfParents(String key)
	{
		for (IProperties parent : parents) {
			Optional<IMethod> oMethod = parent.getMethod(key);
			if (oMethod.isPresent()) return oMethod;
		}
		return Optional.empty();
	}

	public IProperty getOfParents(String key)
	{
		return getMethodOfParents(key).map(m -> m.apply(this)).orElse(new PropertyBasic());
	}

}
