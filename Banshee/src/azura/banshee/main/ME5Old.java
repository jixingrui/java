package azura.banshee.main;

import java.util.zip.CRC32;

import common.algorithm.FastMath;
import common.algorithm.Hex;
import common.algorithm.MD5;
import common.logger.Trace;

public class ME5Old {
	private static byte Windows = 0b00000001;
	private static byte Android = 0b00000010;
	private static byte Ios = 0b00000100;
	private static byte Cpu = 0b00001000;
	public static int totalLength = 44;

	public byte log2;
	public String md5;
	public int crc32;
	public byte os;

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

	public boolean isCpu() {
		return (os & Cpu) != 0;
	}

	public ME5Old isCpu(boolean value) {
		if (value) {
			os |= Cpu;
		} else {
			os &= (~Cpu);
		}
		return this;
	}

	public boolean isWindows() {
		return (os & Windows) != 0;
	}

	public ME5Old isWindows(boolean value) {
		if (value) {
			os |= Windows;
		} else {
			os &= (~Windows);
		}
		return this;
	}

	public boolean isAndroid() {
		return (os & Android) != 0;
	}

	public ME5Old isAndroid(boolean value) {
		if (value) {
			os |= Android;
		} else {
			os &= (~Android);
		}
		return this;
	}

	public boolean isIos() {
		return (os & Ios) != 0;
	}

	public ME5Old isIos(boolean value) {
		if (value) {
			os |= Ios;
		} else {
			os &= (~Ios);
		}
		return this;
	}

	public ME5Old(String me5) {
		String log2String = me5.substring(0, 2);
		md5 = me5.substring(2, 34);
		String crc32String = me5.substring(34, 42);
		String osString = me5.substring(42, 44);

		log2 = Byte.parseByte(log2String, 16);
		crc32 = Integer.parseUnsignedInt(crc32String, 16);
		os = Byte.parseByte(osString, 16);
	}

	public ME5Old(byte[] data) {
		this.log2 = (byte) FastMath.log2(data.length);
		this.md5 = MD5.bytesToString(data);
		CRC32 c = new CRC32();
		c.update(data);
		this.crc32 = (int) c.getValue();
	}

	@Override
	public String toString() {
		return Hex.getHex(log2) + md5 + Hex.getHex(crc32) + Hex.getHex(os);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		byte[] data = new byte[8388608];
		ME5Old me5 = new ME5Old(data);
		Trace.trace(me5);
		me5.isWindows(true);
		Trace.trace(me5);
		me5.isWindows(false);
		Trace.trace(me5);
		me5.isWindows(true);
		Trace.trace(me5);
		me5.isAndroid(true);
		Trace.trace(me5);
		me5.isIos(true);
		Trace.trace(me5);
		me5.isAndroid(false);
		Trace.trace(me5);
		me5.isCpu(true);
		Trace.trace(me5);
	}

}
