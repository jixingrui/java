package common.algorithm.crypto.old;

import java.util.Random;

import common.collections.timer.TimeAxis;

/**
 * This class implements the RC4 (TM) stream cipher.
 * <p>
 * The source code (C version) from which this port was done, is the one posted
 * to the sci.crypt, alt.security, comp.security.misc, and alt.privacy
 * newsgroups on Wed, 14 Sep 1994 06:35:31 GMT by "David Sterndark"
 * &lt;sterndark@netcom.com&gt; (Message-ID: &lt;sternCvKL4B.Hyy@netcom.com&gt;)
 * <p>
 * RC4 (TM) was designed by Ron Rivest, and was previously a trade secret of RSA
 * Data Security, Inc. The algorithm is now in the public domain. The name "RC4"
 * is a trademark of RSA Data Security, Inc.
 * <p>
 * <b>References:</b>
 * <ol>
 * <li>Bruce Schneier, "Section 17.1 RC4," <cite>Applied Cryptography, 2nd
 * edition</cite>, John Wiley &amp; Sons, 1996.
 * </ol>
 * <p>
 * <b>Copyright</b> &copy; 1997 <a href="http://www.systemics.com/">Systemics
 * Ltd</a> on behalf of the <a
 * href="http://www.systemics.com/docs/cryptix/">Cryptix Development Team</a>. <br>
 * All rights reserved.
 * <p>
 * <b>$Revision: 1.6 $</b>
 * 
 * @author Raif S. Naffah
 * @author David Hopwood
 * @since Cryptix 2.2.2
 */
public final class RC4Old { // must be final for security reasons

	/**
	 * Will hold the contents of the current set S-box.
	 */
	private int[] sBox = new int[256];

	/**
	 * The two indices for the S-box computation referred to as i and j in
	 * Schneier.
	 */
	private int x, y;

	public static void main(String[] arguments) {
		Random random = new Random();
		byte[] key = new byte[32];
		byte[] msg = new byte[16];

		random.nextBytes(key);
		random.nextBytes(msg);

		RC4Old alice = new RC4Old(key);
		RC4Old bob = new RC4Old(key);

		TimeAxis.mark();
		for (int i = 0; i < 1000000; i++) {
			byte[] secrit = alice.process(msg);
			byte[] reading = bob.process(secrit);
			if(reading!=null)
				reading=null;
		}
		TimeAxis.show("double");

//		Test.out(key);
//		Test.out(msg);
//		Test.out(secrit);
//		Test.out(reading);
	}

	/**
	 * Constructs an RC4 cipher object, in the UNINITIALIZED state. This calls
	 * the Cipher constructor with <i>implBuffering</i> false,
	 * <i>implPadding</i> false and the provider set to "Cryptix".
	 */
	public RC4Old(byte[] key) {
		makeKey(key);
	}

	public final byte[] process(byte[] data) {
		byte[] buffer = new byte[data.length];
		engineUpdate(data, 0, data.length, buffer, 0);
		return buffer;
	}

	/**
	 * <b>SPI</b>: This is the main engine method for updating data.
	 * <p>
	 * <i>in</i> and <i>out</i> may be the same array, and the input and output
	 * regions may overlap.
	 * 
	 * @param in
	 *            the input data.
	 * @param inOffset
	 *            the offset into in specifying where the data starts.
	 * @param inLen
	 *            the length of the subarray.
	 * @param out
	 *            the output array.
	 * @param outOffset
	 *            the offset indicating where to start writing into the out
	 *            array.
	 * @return the number of bytes written. reports an error.
	 */
	private int engineUpdate(byte[] in, int inOffset, int inLen, byte[] out,
			int outOffset) {
		if (inLen < 0)
			throw new IllegalArgumentException("inLen < 0");

		// Avoid overlapping input and output regions.
		if (in == out
				&& (outOffset >= inOffset && outOffset < inOffset + inLen || inOffset >= outOffset
						&& inOffset < outOffset + inLen)) {
			byte[] newin = new byte[inLen];
			System.arraycopy(in, inOffset, newin, 0, inLen);
			in = newin;
			inOffset = 0;
		}

		rc4(in, inOffset, inLen, out, outOffset);

		return inLen;
	}

	// Own methods
	// ............................................................................

	/**
	 * RC4 encryption/decryption. The input and output regions are assumed not
	 * to overlap.
	 * 
	 * @param in
	 *            the input data.
	 * @param inOffset
	 *            the offset into in specifying where the data starts.
	 * @param inLen
	 *            the length of the subarray.
	 * @param out
	 *            the output array.
	 * @param outOffset
	 *            the offset indicating where to start writing into the out
	 *            array.
	 */
	private void rc4(byte[] in, int inOffset, int inLen, byte[] out,
			int outOffset) {
		int xorIndex, t;

		for (int i = 0; i < inLen; i++) {
			x = (x + 1) & 0xFF;
			y = (sBox[x] + y) & 0xFF;

			t = sBox[x];
			sBox[x] = sBox[y];
			sBox[y] = t;

			xorIndex = (sBox[x] + sBox[y]) & 0xFF;
			out[outOffset++] = (byte) (in[inOffset++] ^ sBox[xorIndex]);
		}
	}

	/**
	 * Expands a user-key to a working key schedule.
	 * <p>
	 * The key bytes are first extracted from the user-key and then used to
	 * build the contents of this key schedule.
	 * <p>
	 * The method's only exceptions are when the user-key's contents are null,
	 * or a byte array of zero length.
	 * 
	 * @param key
	 *            the user-key object to use.
	 * @exception CryptoException
	 *                if one of the following occurs:
	 *                <ul>
	 *                <li> key.getEncoded() == null; <li> The encoded byte array
	 *                form of the key is zero-length;
	 *                </ul>
	 */
	private void makeKey(byte[] userkey) {

		int len = userkey.length;

		x = y = 0;
		for (int i = 0; i < 256; i++)
			sBox[i] = i;

		int i1 = 0, i2 = 0, t;

		for (int i = 0; i < 256; i++) {
			i2 = ((userkey[i1] & 0xFF) + sBox[i] + i2) & 0xFF;

			t = sBox[i];
			sBox[i] = sBox[i2];
			sBox[i2] = t;

			i1 = (i1 + 1) % len;
		}
	}
}
