package common.algorithm;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.zip.Adler32;

public class A32 {

	private static byte[] key = "Adler32".getBytes(Charset.forName("ascii"));

	public static byte[] encode(byte[] data) {
		byte[] tail = compute(data);
		byte[] mix = Arrays.copyOf(data, data.length + 4);
		mix[mix.length - 4] = tail[0];
		mix[mix.length - 3] = tail[1];
		mix[mix.length - 2] = tail[2];
		mix[mix.length - 1] = tail[3];
		return mix;
	}

	public static byte[] decode(byte[] mix) {
		if (mix.length < 4)
			return null;

		byte[] data = Arrays.copyOfRange(mix, 0, mix.length - 4);
		byte[] sumClaim = Arrays.copyOfRange(mix, mix.length - 4, mix.length);
		byte[] sumCompute = compute(data);
		if (Arrays.equals(sumClaim, sumCompute))
			return data;
		else
			return null;
	}

	private static byte[] compute(byte[] data) {
		Adler32 a32 = new Adler32();
		a32.update(data);
		a32.update(key);
		long sum = a32.getValue();
		byte[] tail = new byte[4];
		tail[0] = (byte) ((sum >> 24) & 0xff);
		tail[1] = (byte) ((sum >> 16) & 0xff);
		tail[2] = (byte) ((sum >> 8) & 0xff);
		tail[3] = (byte) ((sum >> 0) & 0xff);
		return tail;
	}
}
