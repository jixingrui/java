package common.collections.buffer;

public class LogicalArray {
	private final byte[] array;
	private final int from;
	public final int length;

	private LogicalArray(byte[] array, int from, int length) {
		this.array = array;
		this.from = from;
		this.length = length;
		if (from + length > array.length)
			throw new Error();
	}

	public static LogicalArray allocate(int capacity) {
		return wrap(new byte[capacity]);
	}

	public static LogicalArray wrap(byte[] array) {
		return new LogicalArray(array, 0, array.length);
	}

	public byte get(int idx) {
		if (idx >= length)
			throw new Error();
		return array[from + idx];
	}

	public void set(int idx, int value) {
		if (idx >= length)
			throw new Error();
		array[from + idx] = (byte) value;
	}

	public void set(int idx, long value) {
		if (idx >= length)
			throw new Error();
		array[from + idx] = (byte) value;
	}

	public LogicalArray subArray(int from, int length) {
		return new LogicalArray(array, this.from + from, length);
	}

	public void copyFrom(byte[] src, int destPos) {
		if (destPos + src.length >= length)
			throw new Error();
		System.arraycopy(src, 0, array, from + destPos, src.length);
	}

	public byte[] toBytes() {
		if (from == 0 && length == array.length) {
			return array;
		} else {
			byte[] sub = new byte[length];
			System.arraycopy(array, from, sub, 0, length);
			return sub;
		}
	}

	public static void copy(LogicalArray src, int srcPos, LogicalArray dest,
			int destPos, int length) {
		System.arraycopy(src.array, src.from + srcPos, dest.array, dest.from
				+ destPos, length);
	}
}
