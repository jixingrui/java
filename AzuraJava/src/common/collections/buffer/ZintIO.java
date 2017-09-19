package common.collections.buffer;

import common.collections.buffer.i.ZintI;

public class ZintIO extends DataArray implements ZintI {

	public ZintIO(LogicalArray array) {
		super(array);
	}

	protected int readerIndex;
	protected int writerIndex;

	public static ZintIO allocate(int capacity) {
		LogicalArray array = LogicalArray.allocate(capacity);
		return new ZintIO(array);
	}

	public static ZintIO wrap(LogicalArray array) {
		ZintIO result = new ZintIO(array);
		result.writerIndex = array.length;
		return result;
	}

	public int readableBytes() {
		return writerIndex - readerIndex;
	}

	public int writeableBytes() {
		return array.length - writerIndex - 1;
	}

	public int dataSize() {
		return writerIndex;
	}

	public ZintIO readPart(int length) {
		LogicalArray la = array.subArray(readerIndex, length);
		ZintIO result = ZintIO.wrap(la);
		readerIndex += length;
		return result;
	}

	// ================== write ================

	public void writeBoolean(boolean value) {
		setBoolean(writerIndex, value);
		writerIndex += 1;
	}

	public void writeByte(int value) {
		setByte(writerIndex, value);
		writerIndex += 1;
	}

	public void writeIntZ(int value) {
		byte[] zb = Zint.writeIntZ(value);
		writeBytes(zb);
	}

	public void writeInt(int value) {
		setInt(writerIndex, value);
		writerIndex += 4;
	}

	public void writeLongZ(long value) {
		byte[] zb = Zint.writeLongZ(value);
		writeBytes(zb);
	}

	public void writeLong(long value) {
		setLong(writerIndex, value);
		writerIndex += 8;
	}

	public void writeDouble(double value) {
		setDouble(writerIndex, value);
		writerIndex += 8;
	}

	public void writeBytes(byte[] src) {
		array.copyFrom(src, writerIndex);
		writerIndex += src.length;
	}

	public void copyFrom(ZintIO src) {
		int length = src.dataSize();
		LogicalArray.copy(src.array, src.readerIndex, this.array,
				this.writerIndex, length);
		writerIndex += length;
	}

	// ================== read ================

	public boolean readBoolean() {
		boolean value = getBoolean(readerIndex);
		readerIndex += 1;
		return value;
	}

	public byte readByte() {
		byte value = getByte(readerIndex);
		readerIndex += 1;
		return value;
	}

	public int readIntZ() {
		return Zint.readIntZ(this);
	}

	public int readInt() {
		int value = getInt(readerIndex);
		readerIndex += 4;
		return value;
	}

	public long readLongZ() {
		return Zint.readLongZ(this);
	}

	public long readLong() {
		long value = getLong(readerIndex);
		readerIndex += 8;
		return value;
	}

	public double readDouble() {
		double value = getDouble(readerIndex);
		readerIndex += 8;
		return value;
	}

	public LogicalArray readBytes(int length) {
		LogicalArray value = array.subArray(readerIndex, length);
		readerIndex += length;
		return value;
	}

	// ================== zint ================

	private int mark;

	public int readUnsignedByte() {
		return readByte() & 0xff;
	}

	public void mark() {
		mark = readerIndex;
	}

	public void rollBack() {
		readerIndex = mark;
	}

}
