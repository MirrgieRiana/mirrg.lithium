package mirrg.lithium.event;

public interface IEventProvider<T>
{

	public IEventRegistry<T> event();

}
