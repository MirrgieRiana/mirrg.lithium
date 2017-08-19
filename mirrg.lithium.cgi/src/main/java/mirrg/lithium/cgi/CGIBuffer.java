package mirrg.lithium.cgi;

import java.io.IOException;
import java.io.InputStream;

import mirrg.lithium.struct.Struct2;

public class CGIBuffer implements AutoCloseable
{

	private CGIBufferPool owner;
	private Struct2<byte[], Integer> requestBuffer = new Struct2<>();
	private Struct2<byte[], Integer> responseBuffer = new Struct2<>();

	public boolean using = false;

	public CGIBuffer(CGIBufferPool owner, int requestBufferSize, int responseBufferSize)
	{
		this.owner = owner;
		this.requestBuffer.x = new byte[requestBufferSize];
		this.requestBuffer.y = 0;
		this.responseBuffer.x = new byte[responseBufferSize];
		this.responseBuffer.y = 0;
	}

	@Override
	public void close()
	{
		using = false;
		owner.onClose(this);
	}

	public Struct2<byte[], Integer> readRequest(InputStream in, ILogger logger) throws HTTPResponse
	{
		try {
			try {
				requestBuffer.y = 0;
				while (true) {
					int len = in.read(requestBuffer.x, requestBuffer.y, requestBuffer.x.length - requestBuffer.y);
					if (len == -1) {
						return requestBuffer;
					}
					requestBuffer.y += len;
					if (requestBuffer.y >= requestBuffer.x.length) {
						if (in.read() == -1) {
							return requestBuffer;
						} else {
							throw HTTPResponse.get(413, "413: Too large request");
						}
					}
				}
			} catch (IOException e) {
				logger.accept(e);
				throw HTTPResponse.get(400);
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.accept(e);
				throw HTTPResponse.get(500);
			}
		}
	}

	public Struct2<byte[], Integer> readResponse(InputStream in, ILogger logger) throws HTTPResponse
	{
		try {
			try {
				responseBuffer.y = 0;
				while (true) {
					int len = in.read(responseBuffer.x, responseBuffer.y, responseBuffer.x.length - responseBuffer.y);
					if (len == -1) {
						return responseBuffer;
					}
					responseBuffer.y += len;
					if (responseBuffer.y >= responseBuffer.x.length) {
						if (in.read() == -1) {
							return responseBuffer;
						} else {
							throw HTTPResponse.get(500, "500: Buffer overflow");
						}
					}
				}
			} catch (IOException e) {
				logger.accept(e);
				throw HTTPResponse.get(500);
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.accept(e);
				throw HTTPResponse.get(500);
			}
		}
	}

}
