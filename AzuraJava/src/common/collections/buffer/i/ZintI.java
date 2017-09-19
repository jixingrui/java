package common.collections.buffer.i;

public interface ZintI {

	int readUnsignedByte();

	int readInt();

	long readLong();

	void mark();

	void rollBack();

}
