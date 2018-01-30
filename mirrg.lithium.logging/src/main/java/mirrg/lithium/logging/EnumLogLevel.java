package mirrg.lithium.logging;

public enum EnumLogLevel
{
	FATAL(1),
	ERROR(2),
	WARN(3),
	INFO(4),
	DEBUG(5),
	TRACE(6),
	;

	public final int level;

	private EnumLogLevel(int level)
	{
		this.level = level;
	}

}
