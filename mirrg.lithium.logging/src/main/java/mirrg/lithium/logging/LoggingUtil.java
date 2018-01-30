package mirrg.lithium.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingUtil
{

	public static LoggingUtil INSTANCE = new LoggingUtil();
	public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss.SSS");

	public String format(String string, EnumLogLevel logLevel)
	{
		return String.format("%s %-7s %s",
			FORMATTER.format(LocalDateTime.now()),
			"[" + logLevel.name() + "]",
			string);
	}

}
