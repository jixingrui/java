package common.logger;

import common.algorithm.Hex;

public class Trace {
	private static StringBuffer sb = new StringBuffer();
	private static int MaxLength = 200;

	public static void dot(String info) {
		sb.append(info);
		if (sb.length() > MaxLength) {
			System.out.print("\n" + info + " ");
			sb = new StringBuffer();
		} else {
			System.out.print(info + " ");
			sb.append(info + " ");
		}
	}

	public static void trace(String info) {
		System.out.println(info);
	}

	public static void trace(int value) {
		trace(value + "");
	}

	public static void dot(int pk) {
		dot(pk + "");
	}

	public static void trace(byte[] bytes) {
		// System.out.print(bytes.length + ":");
		// for (byte b : bytes) {
		// String s = Integer.toHexString(b >= 0 ? b : 256 + b);
		// System.out.print(s);
		// }
		System.out.println(Hex.getHex(bytes));
	}

	public static void trace(boolean truth) {
		System.out.println(truth);
	}

	public static void trace(double value) {
		System.out.println(value);
	}

	public static void trace(Object o) {
		if (o == null)
			trace("null");
		else
			trace(o.toString());
	}

}
