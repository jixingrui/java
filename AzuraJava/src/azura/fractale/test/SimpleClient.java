package azura.fractale.test;

import azura.fractale.netty.handler.FrackUserA;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;
import common.logger.Trace;

public class SimpleClient extends FrackUserA {
	boolean lock = false;

	@Override
	public void connected() {
		Trace.trace("client app connected");

		ZintBuffer zb = new ZintBuffer();
		zb.writeUTFZ("hello server");
		zb.writeZint(33434);
		zb.writeDouble(Math.PI);
		zb.writeUTFZ(" = pi");
		socketSend(zb);
	}

	@Override
	public void socketReceive(ZintReaderI reader) {
		// ZintReaderI zb = new ZintBuffer(data);
		// Trace.trace("client received: " + zb.readUTF());
	}

	@Override
	public void disconnected() {
	}

}
