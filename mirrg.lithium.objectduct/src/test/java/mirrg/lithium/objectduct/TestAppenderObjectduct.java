package mirrg.lithium.objectduct;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

import mirrg.lithium.objectduct.logging.AppenderObjectduct;

public class TestAppenderObjectduct
{

	private static Log LOG = LogFactory.getLog(TestAppenderObjectduct.class);

	@Test
	public void test1()
	{
		ArrayList<String> out = new ArrayList<>();
		ArrayList<String> expected = new ArrayList<String>() {
			{
				add("Message1");
				add("Message2");
				add("Message3");
				add("Message4");
				add("Message5");
				add("Message6");
				//add("Closed");
			}
		};

		LOG.info("Message1");
		LOG.info("Message2");
		LOG.info("Message3");
		AppenderObjectduct.setExporter(new Terminal<LoggingEvent>() {

			@Override
			protected void closeImpl()
			{
				out.add("Closed");
			}

			@Override
			protected void acceptImpl(LoggingEvent t) throws InterruptedException, TerminalClosedException
			{
				out.add(t.getMessage().toString());
			}

		});
		LOG.info("Message4");
		LOG.info("Message5");
		LOG.info("Message6");
		assertEquals(expected, out);
	}

}
