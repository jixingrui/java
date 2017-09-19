package azura.fractale.netty.filter;

import io.netty.buffer.ByteBuf;

import common.collections.buffer.i.ZintI;

public class ZintWrapperBB implements ZintI {

	private ByteBuf bb;
	private int mark;

	public ZintWrapperBB(ByteBuf bb) {
		this.bb = bb;
	}

	@Override
	public int readUnsignedByte() {
		return bb.readUnsignedByte();
	}

	@Override
	public int readInt() {
		return bb.readInt();
	}

	@Override
	public long readLong() {
		return bb.readLong();
	}

	@Override
	public void mark() {
		mark = bb.readerIndex();
	}

	@Override
	public void rollBack() {
		bb.readerIndex(mark);
	}

}
