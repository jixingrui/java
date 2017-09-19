package common.util;

import java.util.Arrays;

public final class ByteW {
	public final byte[] data;

	public ByteW(byte[] data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ByteW)) {
			return false;
		}
		return this == other || Arrays.equals(data, ((ByteW) other).data);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}

	public static ByteW wrap(byte[] data) {
		return new ByteW(data);
	}
}