package azura.fractale.netty.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.io.FileBackedOutputStream;

import common.collections.buffer.Zint;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.sa.ZintReaderStream;

public class ZintDecoder extends ByteToMessageDecoder {
	static final Logger log = Logger.getLogger(ZintDecoder.class);

	static final int OVERSIZE = 10_000_000;
	static final int SMALL_LARGE = 500_000;

	private boolean length_data = true;
	private Integer messageLength;
	private int collectedLength;

	private FileBackedOutputStream fileBuffer = new FileBackedOutputStream(
			SMALL_LARGE);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (length_data) {
			collectedLength = 0;
			messageLength = Zint.readIntZ(new ZintWrapperBB(in));
			// log.debug("receiving = " + messageLength);
			if (messageLength == null || messageLength == 0) {
				return;
			} else if (messageLength < 0) {
				log.error("negative length");
				ctx.disconnect();
			} else if (messageLength > OVERSIZE) {
				log.error("ZintDecoder: oversize: " + (messageLength / 1000)
						+ "/" + (OVERSIZE / 1000) + "kb");
				ctx.disconnect();
			} else {
				length_data = false;
			}
		} else {
			int arrivedLength = in.readableBytes();
			// log.debug("arrived = " + messageLength);

			if (collectedLength + arrivedLength < messageLength) {
				byte[] data = new byte[arrivedLength];
				in.readBytes(data);
				fileBuffer.write(data);
				collectedLength += arrivedLength;
			} else if (collectedLength == 0 && arrivedLength == messageLength) {
				byte[] data = new byte[arrivedLength];
				in.readBytes(data);
				ZintReaderI zb = new ZintBuffer(data);
				out.add(zb);
				// checkRelease(in);

				// log.debug("received = " + messageLength);
				length_data = true;
			} else {
				byte[] data = new byte[messageLength - collectedLength];
				in.readBytes(data);
				fileBuffer.write(data);
				InputStream is = fileBuffer.asByteSource().openStream();
				ZintReaderI zrs = new ZintReaderStream(is);
				out.add(zrs);
				// checkRelease(in);

				// log.debug("received = " + messageLength);
				length_data = true;
				fileBuffer = new FileBackedOutputStream(SMALL_LARGE);

				// ===================== is the fileBuffer going to be garbage
				// collected? ================
			}
		}
	}

	// private void checkRelease(ByteBuf in) {
	// if (in.isReadable())
	// in.release();
	// ReferenceCountUtil.release(in);
	// try {
	// } finally {
	// Trace.trace(in.refCnt());
	// in.release();
	// Trace.trace(in.refCnt());
	// }
	// }

	@Override
	protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		// prevent default
	}
}
