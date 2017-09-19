package azura.fractale.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import common.collections.buffer.i.ZintReaderI;

public class TailInbound extends SimpleChannelInboundHandler<ZintReaderI> {
	private static final Logger log = Logger.getLogger(TailInbound.class);

	private final FrackUserA user;

	public TailInbound(FrackUserA user) {
		this.user = user;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ZintReaderI reader)
			throws Exception {
		user.socketReceive(reader);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//		log.debug("disconnected by exception\n" + cause.getStackTrace());
		log.log(Level.ERROR, "disconnected by exception", cause);
		user.disconnected();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		log.debug("disconnected normally");
		user.disconnected();
	}
}
