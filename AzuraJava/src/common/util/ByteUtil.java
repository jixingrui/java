package common.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class ByteUtil {
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		short s = -20;
//		byte[] b = new byte[2];
//		putShort(b, s, 0);
//		ByteBuffer buf = ByteBuffer.allocate(2);
//		buf.put(b);
//		buf.flip();
//		System.out.println(getShort(b, 0));
//		System.out.println(buf.getShort());
//		System.out.println("***************************");
//		int i = -40;
//		b = new byte[4];
//		putInt(b, i, 0);
//		buf = ByteBuffer.allocate(4);
//		buf.put(b);
//		buf.flip();
//		System.out.println(getInt(b, 0));
//		System.out.println(buf.getInt());
//		System.out.println("***************************");
//		long l = -40;
//		b = new byte[8];
//		putLong(b, l, 0);
//		buf = ByteBuffer.allocate(8);
//		buf.put(b);
//		buf.flip();
//		System.out.println(getLong(b, 0));
//		System.out.println(buf.getLong());
//		System.out.println("***************************");
//	}

	public static void putByte(byte[] bb, byte b, int index) {
		bb[index] = b;
	}

	public static byte getByte(byte[] bb, int index) {
		return bb[index];
	}

	public static byte getByte(byte[] bb) {
		return bb[0];
	}

	// ///////////////////////////////////////////////////////
	public static void putShort(byte bb[], int s, int index) {
		putShort(bb, (short) s, index);
	}

	public static void putShort(byte bb[], short s, int index) {
		bb[index] = (byte) (s >> 8);
		bb[index + 1] = (byte) (s >> 0);
	}

	public static short getShort(byte[] bb, int index) {
		return (short) (((bb[index] << 8) | bb[index + 1] & 0xff));
	}

	public static short getShort(byte[] bb) {
		return getShort(bb, 0);
	}

	public static byte[] short2Byte(short ii) {
		byte[] bb = new byte[2];
		ByteUtil.putShort(bb, ii, 0);
		return bb;
	}

	// ///////////////////////////////////////////////////////
	public static void putInt(byte[] bb, int x, int index) {
		bb[index + 0] = (byte) (x >> 24);
		bb[index + 1] = (byte) (x >> 16);
		bb[index + 2] = (byte) (x >> 8);
		bb[index + 3] = (byte) (x >> 0);
	}

	public static int getInt(byte[] bb, int index) {
		return (int) ((((bb[index + 0] & 0xff) << 24)
				| ((bb[index + 1] & 0xff) << 16)
				| ((bb[index + 2] & 0xff) << 8) | ((bb[index + 3] & 0xff) << 0)));
	}

	public static int getInt(byte[] bb) {
		return getInt(bb, 0);
	}

	public static byte[] int2Byte(int ii) {
		byte[] bb = new byte[4];
		ByteUtil.putInt(bb, ii, 0);
		return bb;
	}

	public static byte[] ints2Byte(int[] ii) {
		byte[] bb = new byte[ii.length * 4];
		for (int i = 0; i < ii.length; i++) {
			ByteUtil.putInt(bb, ii[i], i * 4);
		}
		return bb;
	}

	public static int byte2Int(byte[] bb) {
		return getInt(bb);
	}

	public static int[] byte2Ints(byte[] bb) {
		int[] ii = new int[bb.length / 4];
		for (int i = 0; i < ii.length; i++) {
			getInt(bb, i * 4);
		}
		return ii;
	}

	// /////////////////////////////////////////////////////////
	public static void putLong(byte[] bb, long x, int index) {
		bb[index + 0] = (byte) (x >> 56);
		bb[index + 1] = (byte) (x >> 48);
		bb[index + 2] = (byte) (x >> 40);
		bb[index + 3] = (byte) (x >> 32);
		bb[index + 4] = (byte) (x >> 24);
		bb[index + 5] = (byte) (x >> 16);
		bb[index + 6] = (byte) (x >> 8);
		bb[index + 7] = (byte) (x >> 0);
	}

	public static long getLong(byte[] bb, int index) {
		return ((((long) bb[index + 0] & 0xff) << 56)
				| (((long) bb[index + 1] & 0xff) << 48)
				| (((long) bb[index + 2] & 0xff) << 40)
				| (((long) bb[index + 3] & 0xff) << 32)
				| (((long) bb[index + 4] & 0xff) << 24)
				| (((long) bb[index + 5] & 0xff) << 16)
				| (((long) bb[index + 6] & 0xff) << 8) | (((long) bb[index + 7] & 0xff) << 0));
	}

	public static long getLong(byte[] bb) {
		return getLong(bb, 0);
	}

	public static byte[] long2Byte(long l) {
		byte[] bb = new byte[8];
		putLong(bb, l, 0);
		return bb;
	}

	public static long byte2Long(byte[] bb) {
		return getLong(bb, 0);
	}

	// /////////////////////////////////////////////////////////
	public static void arrayCopy(byte[] dest, byte[] src, int index) {
		System.arraycopy(src, 0, dest, index, src.length);
	}

	public static byte[] arrayRead(byte[] src, int index, int length) {
		return Arrays.copyOfRange(src, index, index + length);
	}

	// /////////////////////////////////////////////////////////
	public static byte[] buffer2Array(ByteBuffer bb) {
		bb.flip();
		byte[] b = new byte[bb.limit()];
		bb.get(b);
		return b;
	}

	// ////////////////////////////////////////////////////////////////
	public static BitSet byte2Bs(byte b) {
		BitSet bs = new BitSet();
		for (int i = 0; i < 8; i++) {
			if ((b & (1 << (i % 8))) > 0) {
				bs.set(i);
			}
		}
		return bs;
	}

	public static BitSet array2Bs(byte[] bytes) {
		BitSet bits = new BitSet();
		for (int i = 0; i < bytes.length * 8; i++) {
			if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
				bits.set(i);
			}
		}
		return bits;
	}

	public static byte[] bs2Array(BitSet bs) {
		byte[] bytes = new byte[bs.length() / 8 + 1];
		for (int i = 0; i < bs.length(); i++) {
			if (bs.get(i)) {
				bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
			}
		}
		return bytes;
	}

	// ////////////////////////////////////////////////////////////////
	public static byte[] getBytes(ByteBuffer bb, int length) {
		byte[] result = new byte[length];
		bb.get(result);
		return result;
	}

	// ////////////////////////////////////////////////////////////////
	public static byte[] string2Bytes(String utf8) {
		try {
			return utf8.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return utf8.getBytes();
		}
	}

	public static String bytes2String(byte[] bytes) {
		String result;
		try {
			result = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			result = new String(bytes);
		}
		return result;
	}
}
