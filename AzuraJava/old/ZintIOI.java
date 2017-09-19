package common.collections.buffer.sa;

public interface ZintIOI {

	public abstract void writeBoolean(boolean value);

	public abstract void writeByte(int value);

	public abstract void writeZint(int value);

	public abstract void writeInt(int value);

	public abstract void writeLong(long value);

	public abstract void writeDouble(double value);

	public abstract void writeBytesZ(byte[] src);

	public abstract void writeUTFZ(String utf8);

	public abstract boolean readBoolean();

	public abstract byte readByte();

	public abstract int readZint();

	public abstract int readInt();

	public abstract long readLong();

	public abstract double readDouble();

	public abstract byte[] readBytesZ();

	public abstract String readUTFZ();

}