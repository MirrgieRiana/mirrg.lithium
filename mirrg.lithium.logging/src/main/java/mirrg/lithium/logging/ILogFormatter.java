package mirrg.lithium.logging;

import java.util.Optional;

public interface ILogFormatter
{

	public String format(String string, Optional<EnumLogLevel> oLogLevel);

}
