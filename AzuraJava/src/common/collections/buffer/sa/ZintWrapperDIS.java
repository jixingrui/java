package common.collections.buffer.sa;

import java.io.DataInputStream;
import java.io.IOException;

import common.collections.buffer.i.ZintI;

public class ZintWrapperDIS implements ZintI {

	private DataInputStream dis;
	private int mark;

	public ZintWrapperDIS(DataInputStream bb) {
		this.dis = bb;
	}

	@Override
	public int readUnsignedByte() {
		try {
			return dis.readUnsignedByte();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mark;
	}

	@Override
	public int readInt() {
		try {
			return dis.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mark;
	}

	@Override
	public long readLong() {
		try {
			return dis.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mark;
	}

	@Override
	public void mark() {
	}

	@Override
	public void rollBack() {
		throw new IllegalAccessError("ZintWrapperDis: shouldn't be here");
	}

}
