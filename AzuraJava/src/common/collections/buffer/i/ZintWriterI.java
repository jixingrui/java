package common.collections.buffer.i;

import common.collections.buffer.ZintBuffer;

public interface ZintWriterI extends BytesI {

	public abstract void writeBoolean(boolean truth);

	/**
	 * @param value
	 *            will be casted to byte internally, to make it look clean
	 */
	public abstract void writeByte(int value);

	public abstract void writeZint(int value);

	public abstract void writeInt(int value);

	public abstract void writeLongZ(long value);

	public abstract void writeLong(long value);

	public abstract void writeDouble(double value);

	/**
	 * @param bytes
	 *            null -> byte[0]
	 */
	public abstract void writeBytesZ(byte[] bytes);

	public abstract void writeBytesZB(ZintBuffer zb);

	/**
	 * @param utf8
	 *            null -> ""
	 */
	public abstract void writeUTFZ(String utf8);

}