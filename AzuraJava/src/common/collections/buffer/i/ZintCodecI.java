package common.collections.buffer.i;

public interface ZintCodecI {
	public abstract void writeTo(ZintWriterI writer);

	public abstract void readFrom(ZintReaderI reader);

}