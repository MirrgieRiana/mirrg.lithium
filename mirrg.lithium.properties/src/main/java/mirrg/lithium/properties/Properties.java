package mirrg.lithium.properties;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;

public class Properties implements IProperties
{

	private ArrayList<Properties> parents = new ArrayList<>();
	private Hashtable<String, IMethod> methods = new Hashtable<>();

	public void addParent(Properties parent)
	{
		parents.add(parent);
	}

	public void put(String key, IMethod method)
	{
		methods.put(key, method);
	}

	public void put(String key, String value)
	{
		methods.put(key, p -> value);
	}

	public Optional<IMethod> getMethodOfParents(String key)
	{
		for (Properties parent : parents) {
			Optional<IMethod> oMethod = parent.getMethod(key);
			if (oMethod.isPresent()) return oMethod;
		}
		return Optional.empty();
	}

	public Optional<IMethod> getMethod(String key)
	{
		IMethod method = methods.get(key);
		if (method != null) return Optional.of(method);
		return getMethodOfParents(key);
	}

	@Override
	public Optional<String> getString(String key)
	{
		return getMethod(key).map(m -> m.apply(this));
	}

	public Optional<String> getStringOfParents(String key)
	{
		return getMethodOfParents(key).map(m -> m.apply(this));
	}

}
