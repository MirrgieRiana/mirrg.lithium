package mirrg.lithium.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LoggingUtil
{

	public static LoggingUtil INSTANCE = new LoggingUtil();
	public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss.SSS");

	public String format(String string, Optional<EnumLogLevel> oLogLevel)
	{
		return String.format("%s %-7s %s",
			FORMATTER.format(LocalDateTime.now()),
			oLogLevel
				.map(l -> "[" + l.name() + "]")
				.orElse(""),
			string);
	}

}
