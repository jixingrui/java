package common.algorithm;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class MD5 {
	public static String bytesToString(byte[] inBytes) {
		byte[] md5Bytes = bytesToBytes(inBytes);
		StringBuffer hexValue = new StringBuffer(32);
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	public static byte[] bytesToBytes(byte[] inBytes) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			return md5.digest(inBytes);
		} catch (Exception e) {
			return null;
		}
	}

	public static String stringToString(String inStr) {
		try {
			byte[] inBytes = inStr.getBytes("UTF-8");
			return bytesToString(inBytes);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
