package mirrg.lithium.cgi;

public interface ILogger
{

	public void accept(Exception e);

	public void accept(String message);

}
