package azura.fractale.netty.filter;

import java.util.List;

import org.apache.log4j.Logger;

import azura.fractale.netty.handler.FrackUserA;
import common.algorithm.MD5;
import common.algorithm.crypto.FrackeyC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ValidatorC extends ByteToMessageDecoder {

	private final Logger log = Logger.getLogger(ValidatorS.class);

	private final FrackUserA user;
	private final FrackeyC fc;

	private byte[] key;

	public ValidatorC(FrackUserA user, FrackeyC fc) {
		this.user = user;
		this.fc = fc;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		if (key != null) {
			in.readByte();
			// byte hello = in.readByte();
			// log.info(hello);
			PipeChanger.change(key, ctx, user, this);
			// user.connected();
		} else if (in.readableBytes() > 36) {
			wrongHeader(ctx);
		} else if (in.readableBytes() < 36) {
			return;
		} else {
			byte[] challenge = new byte[36];
			in.readBytes(challenge);

			byte[] response = fc.respond(challenge);
			key = MD5.bytesToBytes(fc.key);

			ByteBuf bb = ctx.alloc().buffer(response.length);
			bb.writeBytes(response);

			ctx.writeAndFlush(bb);
		}
	}

	private void wrongHeader(ChannelHandlerContext ctx) {
		log.error("ValidatorC: wrong header");
		ctx.disconnect();
	}

}
