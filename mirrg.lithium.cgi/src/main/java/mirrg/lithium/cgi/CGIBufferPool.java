package mirrg.lithium.cgi;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class CGIBufferPool
{

	private final int requestBufferSize;
	private final int responseBufferSize;
	private final int maxCount;
	private final int waitMs;

	private ArrayList<CGIBuffer> cgiBuffers = new ArrayList<>();
	private Object lock = new Object();

	public CGIBufferPool(int requestBufferSize, int responseBufferSize, int maxCount, int waitMs)
	{
		this.requestBufferSize = requestBufferSize;
		this.responseBufferSize = responseBufferSize;
		this.maxCount = maxCount;
		this.waitMs = waitMs;
	}

	public CGIBuffer assign() throws TimeoutException
	{
		long start = System.nanoTime();
		synchronized (lock) {

			// まずは空いているバッファを調べる
			for (CGIBuffer cgiBuffer : cgiBuffers) {
				if (!cgiBuffer.using) {
					cgiBuffer.using = true;
					return cgiBuffer;
				}
			}

			// 次に新しく作れる場合は新しく作る
			if (cgiBuffers.size() < maxCount) {
				CGIBuffer cgiBuffer = new CGIBuffer(this, requestBufferSize, responseBufferSize);
				cgiBuffers.add(cgiBuffer);
				cgiBuffer.using = true;
				return cgiBuffer;
			}

			// それでもできないなら空きが出るまで待つ
			while (true) {
				try {
					lock.wait(waitMs / 10);
				} catch (InterruptedException e) {
					TimeoutException e2 = new TimeoutException();
					e2.initCause(e);
					throw e2;
				}
				for (CGIBuffer cgiBuffer : cgiBuffers) {
					if (!cgiBuffer.using) {
						cgiBuffer.using = true;
						return cgiBuffer;
					}
				}
				if (start + waitMs * 1000 * 1000 < System.nanoTime()) {
					throw new TimeoutException();
				}
			}

		}
	}

	public void onClose(CGIBuffer cgiBuffer)
	{
		synchronized (lock) {
			lock.notify();
		}
	}

}
