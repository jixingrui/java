package azura.fractale.netty.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import com.google.common.io.FileBackedOutputStream;
import common.collections.buffer.Zint;
import common.logger.Trace;

public class CopyOfZintDecoder extends ReplayingDecoder<DecoderState> {
	static final int OVERSIZE = 10000000;
	static final int SMALL_LARGE = 500000;

	private int leftLength;

	private FileBackedOutputStream buffer = new FileBackedOutputStream(
			SMALL_LARGE);

	public CopyOfZintDecoder() {
		super(DecoderState.READ_LENGTH);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		switch (state()) {
		case READ_LENGTH:
			leftLength = Zint.read(in);
			// Trace.trace("zint receive header: " + length);
			if (leftLength < 0) {
				Trace.trace("ZintDecoder: negative length");
				ctx.disconnect();
			} else if (leftLength > OVERSIZE) {
				Trace.trace("ZintDecoder: oversize: " + (leftLength / 1000)
						+ "/" + (OVERSIZE / 1000) + "kb");
				ctx.disconnect();
			} else {
				// Trace.trace("length=" + length);
				checkpoint(DecoderState.READ_DATA);
			}
			break;
		case READ_DATA:
			int arrivedLength = in.readableBytes();

			if (arrivedLength < leftLength) {
				byte[] data = new byte[arrivedLength];
				in.readBytes(data);
				buffer.write(data);
				leftLength -= arrivedLength;
			} else {
				byte[] data = new byte[leftLength];
				in.readBytes(data);
				buffer.write(data);
				checkpoint(DecoderState.READ_LENGTH);
				out.add(buffer.asByteSource().read());
				buffer.reset();
			}

			// Trace.trace("zint receive data: " + length);
			break;
		default:
			throw new Error("ZintDecoder error");
		}
	}
}

enum DecoderState {
	READ_LENGTH, READ_DATA;
}