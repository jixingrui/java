package common.algorithm;

import common.util.Primitives;

public class Hex {

	static final String HEXES = "0123456789abcdef";

	public static String getHex(byte b) {
		return "" + HEXES.charAt((b & 0xF0) >> 4) + HEXES.charAt((b & 0x0F));
	}

	public static String getHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	public static String getHex(int l) {
		byte[] raw = Primitives.toByta(l);
		return getHex(raw);
	}

	public static String getHex(long l) {
		byte[] raw = Primitives.toByta(l);
		return getHex(raw);
	}

	public static String toBinary(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
		for (int i = 0; i < Byte.SIZE * bytes.length; i++)
			sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
					: '1');
		return sb.toString();
	}

}
