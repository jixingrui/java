package common.algorithm.crypto;

import java.util.Arrays;
import java.util.Random;

import common.logger.Trace;
import common.util.ByteUtil;

public class FrackeyS {
	private static final Random random = new Random();

	private RC4 rc4Insecure;
	private DH dh;
	private byte[] rc4_halfKeyServer;
	private byte[] nonce;
	public byte[] challenge;

	/**
	 * @param hint
	 *            length=4
	 * @param key
	 *            length=32
	 */
	public FrackeyS(byte[] hint, RC4 rc4Key) {
		rc4Insecure = rc4Key.clone();
		dh = new DH();
		rc4_halfKeyServer = rc4Insecure.rc4(ByteUtil.int2Byte(dh.getHalfKey()));
		nonce = new byte[28];
		random.nextBytes(nonce);
		// nonce = FastMath.random(28);
		byte[] rc4_nonce = rc4Insecure.rc4(nonce);
		challenge = new byte[36];
		System.arraycopy(hint, 0, challenge, 0, 4);
		System.arraycopy(rc4_nonce, 0, challenge, 4, 28);
		System.arraycopy(rc4_halfKeyServer, 0, challenge, 32, 4);
	}

	/**
	 * @param response
	 *            length=12
	 * @return key please hash it before use to mask the nonce. null means
	 *         invader. note this authentication is insecure, it only detects
	 *         idiots
	 */
	public byte[] extractKey(byte[] response) {

		if (response.length != 32) {
			Trace.trace("should not happen");
			return null;
		}

		byte[] rc4_halfKeyClient = Arrays.copyOfRange(response, 0, 4);
		byte[] rc4_nonce = Arrays.copyOfRange(response, 4, 32);
		byte[] halfKeyClient = rc4Insecure.rc4(rc4_halfKeyClient);
		byte[] nonceBack = rc4Insecure.rc4(rc4_nonce);

		if (!Arrays.equals(nonce, nonceBack)) {
			Trace.trace("idiot invader");
			return null;
		}

		byte[] sk = ByteUtil.int2Byte(dh.getSharedKey(ByteUtil.byte2Int(halfKeyClient)));

		byte[] key = new byte[36];
		System.arraycopy(nonce, 0, key, 0, 28);
		System.arraycopy(sk, 0, key, 28, 4);
		System.arraycopy(halfKeyClient, 0, key, 32, 4);

		// Trace.trace(key);

		return key;
	}

	// public static void main(String[] args) {
	//
	// HintBook book = new HintBook(HintBook.genBook());
	// byte[] hint = book.getHint();
	// byte[] bookKey = book.getKey(hint);
	// RC4 rc4Key = new RC4(bookKey);
	//
	// TimeAxis.mark();
	//
	// for (int i = 0; i < 10000; i++) {
	// FrackeyS s = new FrackeyS(hint, rc4Key);
	// FrackeyC c = new FrackeyC(book);
	// //
	// byte[] s2c = s.challenge;
	// byte[] c2s = c.respond(s2c);
	// byte[] fkc = c.key;
	// byte[] fks = s.extractKey(c2s);
	//
	// if (!Arrays.equals(fkc, fks))
	// Trace.trace("error");
	// // else
	// // Trace.trace("right");
	// // Trace.trace(s2c);
	// // Trace.trace(c2s);
	// // Trace.trace(fkc);
	// // Trace.trace(fks);
	// }
	//
	// TimeAxis.show("secure channel");
	//
	// System.exit(0);
	// }
}
