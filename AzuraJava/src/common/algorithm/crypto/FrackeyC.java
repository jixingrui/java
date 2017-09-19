package common.algorithm.crypto;

import java.util.Arrays;

import common.collections.timer.TimeAxis;
import common.util.ByteUtil;

public class FrackeyC {

	private HintBook book;
	public byte[] key;

	public FrackeyC(HintBook book) {
		this.book = book;
	}

	/**
	 * @param challenge
	 *            length=16
	 * @return KeyExchangerC.key is now ready
	 */
	public byte[] respond(byte[] challenge) {
		if (challenge.length != 36)
			return null;

		byte[] hint = Arrays.copyOfRange(challenge, 0, 4);
		byte[] rc4_nonce = Arrays.copyOfRange(challenge, 4, 32);
		byte[] rc4_halfKeyServer = Arrays.copyOfRange(challenge, 32, 36);

		byte[] bookKey = book.getKey(hint);
		if (bookKey == null) {
			// guess what this is?
			bookKey = TimeAxis.random(32);
		}

		RC4 rc4Insecure = new RC4(bookKey);
		byte[] halfKeyServer = rc4Insecure.rc4(rc4_halfKeyServer);
		byte[] nonce = rc4Insecure.rc4(rc4_nonce);

		DH dh = new DH();
		byte[] halfKeyClient = ByteUtil.int2Byte(dh.getHalfKey());
		byte[] sk = ByteUtil.int2Byte(dh.getSharedKey(ByteUtil
				.byte2Int(halfKeyServer)));

		byte[] rc4_halfKeyClient = rc4Insecure.rc4(halfKeyClient);
		byte[] rc4_nonceBack = rc4Insecure.rc4(nonce);

		byte[] result = new byte[32];
		System.arraycopy(rc4_halfKeyClient, 0, result, 0, 4);
		System.arraycopy(rc4_nonceBack, 0, result, 4, 28);

		key = new byte[36];
		System.arraycopy(nonce, 0, key, 0, 28);
		System.arraycopy(sk, 0, key, 28, 4);
		System.arraycopy(halfKeyClient, 0, key, 32, 4);

		return result;
	}

	// public static void main(String[] args) {
	// byte[] fake = TimeAxis.random(16);
	// HintBook book = new HintBook(HintBook.genBook());
	// FrackeyC c = new FrackeyC(book);
	//
	// TimeAxis.mark();
	// for (int i = 0; i < 10; i++) {
	// byte[] res = c.respond(fake);
	// Trace.trace(res);
	// Trace.trace(c.key);
	// }
	// TimeAxis.show("fake");
	//
	// // Trace.trace(res);
	//
	// System.exit(0);
	// }
}
