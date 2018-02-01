package mirrg.lithium.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LogFormatterSimple implements ILogFormatter
{

	public static final LogFormatterSimple INSTANCE = new LogFormatterSimple("uuuu/MM/dd HH:mm:ss.SSS");

	private DateTimeFormatter formatter;

	public LogFormatterSimple(String pattern)
	{
		formatter = DateTimeFormatter.ofPattern(pattern);
	}

	@Override
	public String format(String string, Optional<EnumLogLevel> oLogLevel)
	{
		return String.format("%s %-7s %s",
			formatter.format(LocalDateTime.now()),
			oLogLevel
				.map(l -> "[" + l.name() + "]")
				.orElse(""),
			string);
	}

}
