package common.collections.buffer;

import common.collections.buffer.i.ZintI;

public class Zint {

	public static byte[] writeIntZ(int value) {
		byte[] result = null;
		int abs = Math.abs(value);

		if (value >= 0 && value < 128) {
			result = new byte[1];
			result[0] = (byte) (128 | value);
		} else if (value < 0 && value > -64) {
			result = new byte[1];
			result[0] = (byte) (64 | abs);
		} else if (value >= 128 && value < 32 * 256) {
			result = new byte[2];
			result[0] = (byte) (32 | (value >>> 8));
			result[1] = (byte) (value);
		} else if (value <= -64 && value > -16 * 256) {
			result = new byte[2];
			result[0] = (byte) (16 | (abs >>> 8));
			result[1] = (byte) (abs);
		} else if (value >= 32 * 256 && value < 8 * 256 * 256) {
			result = new byte[3];
			result[0] = (byte) (8 | (value >>> 16));
			result[1] = (byte) (value >>> 8);
			result[2] = (byte) (value);
		} else if (value <= -16 * 256 && value > -4 * 256 * 256) {
			result = new byte[3];
			result[0] = (byte) (4 | (abs >>> 16));
			result[1] = (byte) (abs >>> 8);
			result[2] = (byte) (abs);
		} else {
			result = new byte[5];
			result[0] = 1;
			result[1] = (byte) (value >> 24);
			result[2] = (byte) (value >> 16);
			result[3] = (byte) (value >> 8);
			result[4] = (byte) (value >> 0);
		}

		return result;
	}

	/**
	 * @return null if source does not have enough data for decoding
	 */
	public static Integer readIntZ(ZintI source) {
		source.mark();
		try {

			int b0 = source.readUnsignedByte();
			int value = -1;
			switch (Integer.numberOfLeadingZeros(b0) - 24) {
			case 0: {
				value = b0 & 127;
				break;
			}
			case 1: {
				value *= b0 & 63;
				break;
			}
			case 2: {
				int b1 = source.readUnsignedByte();
				value = (b0 & 31) << 8 | b1;
			}
				break;
			case 3: {
				int b1 = source.readUnsignedByte();
				value *= (b0 & 15) << 8 | b1;
			}
				break;
			case 4: {
				int b1 = source.readUnsignedByte();
				int b2 = source.readUnsignedByte();
				value = ((b0 & 7) << 16) | (b1 << 8) | b2;
			}
				break;
			case 5: {
				int b1 = source.readUnsignedByte();
				int b2 = source.readUnsignedByte();
				value *= ((b0 & 3) << 16) | (b1 << 8) | b2;
			}
				break;
			default: {
				value = source.readInt();
			}
			}

			return value;
		} catch (Exception e) {
			source.rollBack();
			return null;
		}
	}

	// ================== long and lazy ===================

	public static byte[] writeLongZ(long value) {
		byte[] result = null;
		long abs = Math.abs(value);

		if (value >= 0 && value < 128) {
			result = new byte[1];
			result[0] = (byte) (128 | value);
		} else if (value < 0 && value > -64) {
			result = new byte[1];
			result[0] = (byte) (64 | abs);
		} else if (value >= 128 && value < 32 * 256) {
			result = new byte[2];
			result[0] = (byte) (32 | (value >>> 8));
			result[1] = (byte) (value);
		} else if (value <= -64 && value > -16 * 256) {
			result = new byte[2];
			result[0] = (byte) (16 | (abs >>> 8));
			result[1] = (byte) (abs);
		} else if (value >= 32 * 256 && value < 8 * 256 * 256) {
			result = new byte[3];
			result[0] = (byte) (8 | (value >>> 16));
			result[1] = (byte) (value >>> 8);
			result[2] = (byte) (value);
		} else if (value <= -16 * 256 && value > -4 * 256 * 256) {
			result = new byte[3];
			result[0] = (byte) (4 | (abs >>> 16));
			result[1] = (byte) (abs >>> 8);
			result[2] = (byte) (abs);
		} else {
			result = new byte[9];
			result[0] = 1;
			result[1] = (byte) (value >> 56);
			result[2] = (byte) (value >> 48);
			result[3] = (byte) (value >> 40);
			result[4] = (byte) (value >> 32);
			result[5] = (byte) (value >> 24);
			result[6] = (byte) (value >> 16);
			result[7] = (byte) (value >> 8);
			result[8] = (byte) (value >> 0);
		}

		return result;
	}

	public static Long readLongZ(ZintI source) {
		source.mark();
		try {

			int b0 = source.readUnsignedByte();
			long value = -1;
			switch (Integer.numberOfLeadingZeros(b0) - 24) {
			case 0: {
				value = b0 & 127;
				break;
			}
			case 1: {
				value *= b0 & 63;
				break;
			}
			case 2: {
				int b1 = source.readUnsignedByte();
				value = (b0 & 31) << 8 | b1;
			}
				break;
			case 3: {
				int b1 = source.readUnsignedByte();
				value *= (b0 & 15) << 8 | b1;
			}
				break;
			case 4: {
				int b1 = source.readUnsignedByte();
				int b2 = source.readUnsignedByte();
				value = ((b0 & 7) << 16) | (b1 << 8) | b2;
			}
				break;
			case 5: {
				int b1 = source.readUnsignedByte();
				int b2 = source.readUnsignedByte();
				value *= ((b0 & 3) << 16) | (b1 << 8) | b2;
			}
				break;
			default: {
				value = source.readLong();
			}
			}

			return value;
		} catch (Exception e) {
			source.rollBack();
			return null;
		}
	}

	// ================== tests =====================
	// public static void main(String[] args) {
	// // validTest();
	// // benchTest();
	// // loop(0, 1000000);
	// // loop(-1000000, 0);
	//
	// // Long start = (long) (Integer.MAX_VALUE - 100);
	// // Long end = (long) (Integer.MAX_VALUE + 100);
	//
	// Long start = -10000L;
	// Long end = 10000L;
	//
	// loop(start, end);
	// }

	public static void loop(int start, int end) {
		for (int i = start; i <= end; i++) {
			byte[] zip = writeIntZ(i);
			ZintIO io = new ZintIO(LogicalArray.wrap(zip));
			Integer back = readIntZ(io);
			check(i, back);
		}
	}

	public static void loop(long start, long end) {
		for (long i = start; i <= end; i++) {
			byte[] zip = writeLongZ(i);
			ZintIO io = new ZintIO(LogicalArray.wrap(zip));
			Long back = readLongZ(io);
			check(i, back);
		}
	}

	private static void check(int original, Integer back) {
		if (back == null || original != back) {
			error(original, back);
		}
	}

	private static void check(Long original, Long back) {
		if (original == null || back == null) {
			error(original, back);
		}
	}

	private static void error(int original, Integer back) {
		System.out.println("error: " + original + " " + back);
	}

	private static void error(Long original, Long back) {
		System.out.println("error: " + original + " " + back);
	}

	// private static void benchTest() {
	// Random rand = new Random();
	// long start = System.currentTimeMillis();
	// for (int i = 0; i < 10000000; i++) {
	// // int v = rand.nextInt();
	// // unzip(zip(v));
	// }
	// long gap = System.currentTimeMillis() - start;
	// System.out.println("time: " + gap);
	// }

	public static void validTest() {
		int[] tv = new int[] { -123456789, -12345678, -1234567, -123456, -12345, -1234, -123, -12, -1, 0, 1, 12, 123,
				1234, 12345, 123456, 1234567, 12345678, 123456789 };

		for (int v = 0; v <= 257; v++) {
			test(v);
		}

		System.out.println("============");

		for (int v = 0; v >= -257; v--) {
			test(v);
		}

		System.out.println("============");

		for (int v : tv)
			test(v);
	}

	private static void test(int value) {
		byte[] zip = Zint.writeIntZ(value);
		ZintIO io = new ZintIO(LogicalArray.wrap(zip));
		String out = zip.length + "\t" + value + " " + Zint.readIntZ(io);
		System.out.println(out);
	}
}
