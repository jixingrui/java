package azura.fractale.netty.filter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.log4j.Logger;

import azura.fractale.netty.handler.FrackUserA;

import common.algorithm.Hex;
import common.algorithm.MD5;
import common.algorithm.crypto.FrackeyS;

public class ValidatorS extends ByteToMessageDecoder {

	private final Logger log = Logger.getLogger(ValidatorS.class);

	private final FrackUserA user;
	private final FrackeyS fs;
	private volatile boolean failed;

	public ValidatorS(FrackUserA user, FrackeyS fs) {
		this.user = user;
		this.fs = fs;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ByteBuf out = Unpooled.directBuffer(36);
		out.writeBytes(fs.challenge);
		ctx.writeAndFlush(out);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {

		if (failed)
			return;
		else if (in.readableBytes() < 32)
			return;
		else if (in.readableBytes() > 32) {
			byte[] responce = new byte[in.readableBytes()];
			in.readBytes(responce);
			fail(responce, ctx);
		} else {
			byte[] responce = new byte[32];
			in.readBytes(responce);

			byte[] key = fs.extractKey(responce);
			key = MD5.bytesToBytes(key);

			if (key != null) {

				ByteBuf bb = Unpooled.directBuffer(1);
				bb.writeByte(123);
				ctx.writeAndFlush(bb);
				
				PipeChanger.change(key, ctx, user, this);

				// log.debug("validated");
//				user.connected();

			} else {
				fail(responce, ctx);
			}
		}
	}

	private void fail(byte[] responce, ChannelHandlerContext ctx) {
		failed = true;
		String host = ((InetSocketAddress) ctx.channel().remoteAddress())
				.getAddress().getHostAddress();
		log.error("validation failure from client " + host + " content = "
				+ Hex.getHex(responce));
		ctx.disconnect();
	}

}