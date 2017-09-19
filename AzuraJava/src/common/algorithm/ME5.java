package common.algorithm;

import java.util.zip.CRC32;

public class ME5 {
	public static final byte data = 0;
	public static final byte atf_win = 1;
	public static final byte atf_android = 2;
	public static final byte atf_ios = 3;
	public static final byte bitmap = 4;

	public static int fullLength = 44;

	public byte log2;
	public String md5;
	public int crc32;
	public byte fileType;

	/**
	 * @me5t [0-13) [1,8k)
	 * @me5s [13-17) [8k,128k)
	 * @me5m [17-23) [128k,8m)
	 * @me5l [23-28) [8m,200m)
	 * @me5x [28-?) [200m-?)
	 */
	public String getSize() {
		if (log2 < 13)
			return "t";
		else if (log2 < 17)
			return "s";
		else if (log2 < 23)
			return "m";
		else if (log2 < 28)
			return "l";
		else
			return "x";
	}

	public ME5(String me5) {
		String log2String = me5.substring(0, 2);
		md5 = me5.substring(2, 34);
		String crc32String = me5.substring(34, 42);
		String osString = me5.substring(42, 44);

		log2 = Byte.parseByte(log2String, 16);
		crc32 = Integer.parseUnsignedInt(crc32String, 16);
		fileType = Byte.parseByte(osString, 16);
	}

	public ME5(byte[] data, byte type) {
		this.log2 = (byte) FastMath.log2(data.length);
		this.md5 = MD5.bytesToString(data);
		CRC32 c = new CRC32();
		c.update(data);
		this.crc32 = (int) c.getValue();
		this.fileType = type;
	}

	// public ME5 setType(byte type) {
	// this.fileType = type;
	// return this;
	// }

	@Override
	public String toString() {
		return Hex.getHex(log2) + md5 + Hex.getHex(crc32)
				+ Hex.getHex(fileType);
	}

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
		// byte[] data = new byte[8388608];
		// ME5 me5 = new ME5(data);
		// Trace.trace(me5);
		// me5.isWindows(true);
		// Trace.trace(me5);
		// me5.isWindows(false);
		// Trace.trace(me5);
		// me5.isWindows(true);
		// Trace.trace(me5);
		// me5.isAndroid(true);
		// Trace.trace(me5);
		// me5.isIos(true);
		// Trace.trace(me5);
		// me5.isAndroid(false);
		// Trace.trace(me5);
		// me5.isCpu(true);
		// Trace.trace(me5);
//	}

}
