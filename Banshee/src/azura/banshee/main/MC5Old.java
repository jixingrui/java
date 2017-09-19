package azura.banshee.main;

import java.util.zip.CRC32;

import common.algorithm.FastMath;
import common.algorithm.Hex;
import common.algorithm.MD5;
import common.logger.Trace;

public class MC5Old {

	public byte log2;
	public String md5;
	public int crc32;

	public static int fullLength = 36;
	
	/**
	 * @mc5t [0-13) [1,8k)
	 * @mc5s [13-17) [8k,128k)
	 * @mc5m [17-23) [128k,8m)
	 * @mc5l [23-28) [8m,200m)
	 * @mc5x [28-?) [200m-?)
	 */
	public static String getSize(String mc5) {
		MC5Old raw = new MC5Old(mc5);
		if (raw.log2 < 13)
			return "t";
		else if (raw.log2 < 17)
			return "s";
		else if (raw.log2 < 23)
			return "m";
		else if (raw.log2 < 28)
			return "l";
		else
			return "x";
	}

	public MC5Old(String mc5) {
		String log2s = mc5.substring(0, 2);
		md5 = mc5.substring(2, 34);
		String crc32s = mc5.substring(34, 42);

		log2 = Byte.parseByte(log2s, 16);
		crc32 = Integer.parseUnsignedInt(crc32s, 16);
	}

	public MC5Old(byte[] data) {
		this.log2 = (byte) FastMath.log2(data.length);
		this.md5 = MD5.bytesToString(data);
		CRC32 c = new CRC32();
		c.update(data);
		this.crc32 = (int) c.getValue();
	}

	@Override
	public String toString() {
		return Hex.getHex(log2) + md5 + Hex.getHex(crc32);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		byte[] data = new byte[8388608];
		MC5Old mc5 = new MC5Old(data);
		Trace.trace(mc5.log2 + "," + MC5Old.getSize(mc5.toString()));
	}

}
