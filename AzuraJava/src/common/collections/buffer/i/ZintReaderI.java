package common.collections.buffer.i;

import common.collections.buffer.ZintBuffer;

public interface ZintReaderI {

	public abstract boolean readBoolean();

	public abstract byte readByte();

	public abstract int readZint();

	public abstract int readInt();

	public abstract long readLongZ();

	public abstract long readLong();

	public abstract double readDouble();

	/**
	 * @return byte[0] but never null;
	 */
	public abstract byte[] readBytesZ();

	public abstract ZintBuffer readBytesZB();

	/**
	 * @return "" but never null
	 */
	public abstract String readUTFZ();

	public abstract boolean hasRemaining();

	public abstract void close();

	public abstract byte[] toBytes();

}