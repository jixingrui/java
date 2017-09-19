package azura.fractale.netty.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import org.apache.log4j.Logger;

import common.collections.buffer.Zint;

public class ZintEncoder extends MessageToMessageEncoder<byte[]> {
	static final Logger log = Logger.getLogger(ZintEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] msg,
			List<Object> out) throws Exception {
//		log.debug("send = " + msg.length);
		out.add(Zint.writeIntZ(msg.length));
		out.add(msg);
	}

}
