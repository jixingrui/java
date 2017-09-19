package common.algorithm;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class MC5 {
	/**
	 * log2=2, md5=32, crc32=8
	 */
	public static final int fullLength = 42;

	public byte log2;
	public String md5;
	/**
	 * crc32(log2+md5+file)
	 */
	public int crc;

	/**
	 * @S [0-17) [0,128k)
	 * @M [17-24) [128k,16m)
	 * @L [24-?) (16m-?)
	 */
	public static String getSize3(String mc5) {
		String log2s = mc5.substring(0, 2);
		int log2 = Byte.parseByte(log2s, 16);

		if (log2 < 17)
			return "S";
		else if (log2 < 24)
			return "M";
		else
			return "L";
	}

	public MC5(String mc5) {
		String log2s = mc5.substring(0, 2);
		log2 = Byte.parseByte(log2s, 16);

		md5 = mc5.substring(2, 34);

		String crc32s = mc5.substring(34, 42);
		crc = Integer.parseUnsignedInt(crc32s, 16);
	}

	public MC5(byte[] data) {
		this.log2 = (byte) FastMath.log2(data.length);
		this.md5 = MD5.bytesToString(data);
		CRC32 c = new CRC32();
		c.update(log2);
		c.update(md5.getBytes(StandardCharsets.UTF_8));
		c.update(data);
		this.crc = (int) c.getValue();
	}

	@Override
	public String toString() {
		return Hex.getHex(log2) + md5 + Hex.getHex(crc);
	}

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		byte[] data = new byte[16777216];
//		MC5 mc5 = new MC5(data);
//		Trace.trace(mc5.log2 + "," + MC5.getSize3(mc5.toString()) + "   " + mc5);
//
//		// long ui = 2477112321L;
//		int ni = -1817854975;
//		Trace.trace(ni);
//		String hex = Hex.getHex(ni);
//		Trace.trace(hex);
//		int back = Integer.parseUnsignedInt(hex, 16);
//		Trace.trace(back);
//		String backHex = Hex.getHex(back);
//		Trace.trace(backHex);
//	}

}
