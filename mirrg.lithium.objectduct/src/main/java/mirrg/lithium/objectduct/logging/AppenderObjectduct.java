package mirrg.lithium.objectduct.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import mirrg.lithium.objectduct.Terminal;
import mirrg.lithium.objectduct.TerminalClosedException;
import mirrg.lithium.objectduct.inventories.Hopper;
import mirrg.lithium.objectduct.inventories.Objectduct;

public class AppenderObjectduct extends AppenderSkeleton
{

	private static class ObjectductImpl extends Objectduct
	{

		private static Object lock = new Object();

		//

		private Hopper<LoggingEvent> hopper;
		private Terminal<LoggingEvent> exporter;

		public Terminal<LoggingEvent> getImporter()
		{
			return hopper.getImporter();
		}

		public void setExporter(Terminal<LoggingEvent> exporter)
		{
			synchronized (lock) {
				this.exporter = exporter;
				lock.notifyAll();
			}
		}

		@Override
		protected void initInventories() throws Exception
		{
			add(hopper = new Hopper<>(10000));
		}

		@Override
		protected void initConnections() throws Exception
		{
			hopper.setExporter(new Terminal<LoggingEvent>() {

				@Override
				protected void acceptImpl(LoggingEvent t) throws InterruptedException, TerminalClosedException
				{
					synchronized (lock) {
						while (exporter == null) {
							lock.wait();
						}
						exporter.accept(t);
					}
				}

				@Override
				protected void closeImpl()
				{
					synchronized (lock) {
						while (exporter == null) {
							try {
								lock.wait();
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						}
						exporter.close();
					}
				}

			});
		}

	}

	private static ObjectductImpl objectductImpl = new ObjectductImpl();
	static {
		try {
			objectductImpl.init();
			objectductImpl.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void setExporter(Terminal<LoggingEvent> exporter)
	{
		objectductImpl.setExporter(exporter);
	}

	//

	@Override
	public void close()
	{
		objectductImpl.getImporter().close();
	}

	@Override
	public boolean requiresLayout()
	{
		return false;
	}

	@Override
	protected void append(LoggingEvent event)
	{
		try {
			objectductImpl.getImporter().accept(event);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (TerminalClosedException e) {
			e.printStackTrace();
		}
	}

}
