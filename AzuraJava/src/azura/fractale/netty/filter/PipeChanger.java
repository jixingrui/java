package azura.fractale.netty.filter;

import azura.fractale.netty.handler.ExceptionHandler;
import azura.fractale.netty.handler.FrackUserA;
import azura.fractale.netty.handler.TailInbound;
import common.algorithm.crypto.RC4;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

public class PipeChanger {

	public static void change(byte[] key, ChannelHandlerContext ctx,
			FrackUserA user, ChannelHandler validator) {
		
		RC4 rc4 = new RC4(key);
		ChannelPipeline pipe = ctx.pipeline();
		pipe.addLast("in1", new RC4Decoder(rc4))
				.addLast("in2", new ZintDecoder())
				.addLast("out1", new RC4Encoder(rc4.clone()))
				.addLast("out2", new ZintEncoder())
				.addLast("in3", new TailInbound(user))
				.addLast("exception",new ExceptionHandler());
		user.setSink(pipe.lastContext());
		pipe.remove(validator);
		user.connected();
	}
}