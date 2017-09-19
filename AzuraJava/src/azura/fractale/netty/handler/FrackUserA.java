package azura.fractale.netty.handler;

import org.apache.log4j.Logger;

import common.collections.buffer.i.OutI;
import common.collections.buffer.i.ZintReaderI;
import io.netty.channel.ChannelHandlerContext;

public abstract class FrackUserA implements OutI{

	public static Logger log = Logger.getLogger(FrackUserA.class);

	private ChannelHandlerContext ctx;

	public void setSink(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public void socketSend(byte[] data) {
//		log.debug("socket send "+data.length+"bytes");
		if (ctx == null)
			throw new Error();
		ctx.writeAndFlush(data);
	}

	public void socketSend(ZintReaderI data) {
		socketSend(data.toBytes());
	}
	
	@Override
	public void out(byte[] data) {
		socketSend(data);
	}

	public void disconnect() {
		ctx.disconnect();
	}

	public void connected() {
	};

	public void disconnected() {
	};

	public abstract void socketReceive(ZintReaderI reader);
}
