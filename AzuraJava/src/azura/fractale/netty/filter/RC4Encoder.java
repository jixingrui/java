package azura.fractale.netty.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.apache.log4j.Logger;

import common.algorithm.crypto.RC4;

public class RC4Encoder extends MessageToByteEncoder<byte[]> {
	static final Logger log = Logger.getLogger(RC4Encoder.class);

	private RC4 rc4;

	public RC4Encoder(RC4 rc4) {
		this.rc4 = rc4;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out)
			throws Exception {
		
//		log.debug("sent "+msg.length);

		byte[] cypher = rc4.rc4(msg);
		out.writeBytes(cypher);
	}

}
