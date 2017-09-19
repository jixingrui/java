package azura.fractale.netty.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.apache.log4j.Logger;

import common.algorithm.crypto.RC4;

public class RC4Decoder extends ByteToMessageDecoder {
	static final Logger log = Logger.getLogger(RC4Decoder.class);

	private RC4 rc4;

	public RC4Decoder(RC4 rc4) {
		this.rc4 = rc4;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		
//		log.debug("received "+in.readableBytes());

		byte[] cypher = new byte[in.readableBytes()];
		in.readBytes(cypher);

		byte[] plain = rc4.rc4(cypher);
		ByteBuf bb = ctx.alloc().buffer(plain.length);
		bb.writeBytes(plain);

		out.add(bb);
	}
}
