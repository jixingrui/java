package common.collections.buffer.sa;

import java.nio.ByteBuffer;

import common.collections.buffer.ZintI;

public class ZintWrapperSA implements ZintI {

	private ByteBuffer bb;

	public ZintWrapperSA(ByteBuffer bb) {
		this.bb = bb;
	}

	@Override
	public int readUnsignedByte() {
		return bb.get() & 0xff;
	}

	@Override
	public int readInt() {
		return bb.getInt();
	}

	@Override
	public void mark() {
		bb.mark();
	}

	@Override
	public void rollBack() {
		bb.reset();
	}

}
