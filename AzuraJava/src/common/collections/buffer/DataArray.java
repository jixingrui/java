package common.collections.buffer;

public class DataArray {

	protected LogicalArray array;

	public DataArray(LogicalArray array) {
		this.array = array;
	}

	public void expand(int more) {
		LogicalArray la = LogicalArray.allocate(array.length + more);
		LogicalArray.copy(array, 0, la, 0, array.length);
		array = la;
	}

	public boolean getBoolean(int idx) {
		byte b = getByte(idx);
		return b != 0;
	}

	public byte getByte(int idx) {
		return array.get(idx);
	}

	public short getShort(int idx) {
		return (short) (array.get(idx) << 8 | array.get(idx + 1) & 0xFF);
	}

	public int getInt(int idx) {
		return (array.get(idx) & 0xff) << 24
				| (array.get(idx + 1) & 0xff) << 16
				| (array.get(idx + 2) & 0xff) << 8 | array.get(idx + 3) & 0xff;
	}

	public long getLong(int idx) {
		return ((long) array.get(idx) & 0xff) << 56
				| ((long) array.get(idx + 1) & 0xff) << 48
				| ((long) array.get(idx + 2) & 0xff) << 40
				| ((long) array.get(idx + 3) & 0xff) << 32
				| ((long) array.get(idx + 4) & 0xff) << 24
				| ((long) array.get(idx + 5) & 0xff) << 16
				| ((long) array.get(idx + 6) & 0xff) << 8
				| (long) array.get(idx + 7) & 0xff;
	}

	public double getDouble(int idx) {
		return Double.longBitsToDouble(getLong(idx));
	}

	public DataArray setBoolean(int idx, boolean value) {
		int b = (value) ? 1 : 0;
		return setByte(idx, b);
	}

	public DataArray setByte(int idx, int value) {
		array.set(idx, value);
		return this;
	}

	public DataArray setShort(int idx, int value) {
		array.set(idx, value >>> 8);
		array.set(idx + 1, value);
		return this;
	}

	public DataArray setInt(int idx, int value) {
		array.set(idx, value >>> 24);
		array.set(idx + 1, value >>> 16);
		array.set(idx + 2, value >>> 8);
		array.set(idx + 3, value);
		return this;
	}

	public DataArray setLong(int idx, long value) {
		array.set(idx, value >>> 56);
		array.set(idx + 1, value >>> 48);
		array.set(idx + 2, value >>> 40);
		array.set(idx + 3, value >>> 32);
		array.set(idx + 4, value >>> 24);
		array.set(idx + 5, value >>> 16);
		array.set(idx + 6, value >>> 8);
		array.set(idx + 7, value);
		return this;
	}

	public DataArray setDouble(int idx, double value) {
		return setLong(idx, Double.doubleToRawLongBits(value));
	}

}
