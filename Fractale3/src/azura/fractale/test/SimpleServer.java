package azura.fractale.test;

import azura.fractale.netty.handler.FrackUserA;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;
import common.logger.Trace;

public class SimpleServer extends FrackUserA {
	boolean lock = false;

	@Override
	public void connected() {
		Trace.trace("server app connected");

		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

		ZintBuffer zb = new ZintBuffer();
		zb.writeUTFZ("hello client");
		socketSend(zb);
	}

	@Override
	public void socketReceive(ZintReaderI reader) {
		// Trace.trace("server received: " + data.length);
		//
		// ZintReaderI zb = new ZintBuffer(data);
		// String hello = zb.readUTF();
		// int num1 = zb.readZint();
		// double num2 = zb.readDouble();
		// String over = zb.readUTF();
		// Trace.trace("server received: " + hello + " " + num1 + " " + num2 +
		// " "
		// + over);
	}

	@Override
	public void disconnected() {
	}

}
