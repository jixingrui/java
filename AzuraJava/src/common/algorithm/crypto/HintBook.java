package common.algorithm.crypto;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

import common.algorithm.Stega;

public class HintBook {
	private static final int hintSize = 4;
	private static final int keySize = 32;
	private static final int bookSize = 360;
	public static final int dataLength = (hintSize + keySize) * bookSize;

	private final byte[] book;
	private final HashMap<ByteArrayWrapper, Integer> hint_index = new HashMap<ByteArrayWrapper, Integer>();

	/**
	 * @param data
	 *            use HintBook.genBook()
	 */
	public HintBook(byte[] data) {
		if (data.length == dataLength) {
			book = data;
			for (int i = 0; i < bookSize; i++) {
				byte[] hint = new byte[hintSize];
				System.arraycopy(book, i * hintSize, hint, 0, hintSize);
				ByteArrayWrapper baw = new ByteArrayWrapper(hint);
				hint_index.put(baw, i);
			}
		} else
			throw new IllegalArgumentException("data length must be " + dataLength);
	}

	/**
	 * @param hint
	 * @return null if not found
	 */
	public byte[] getKey(byte[] hint) {
		ByteArrayWrapper baw = new ByteArrayWrapper(hint);
		Integer index = hint_index.get(baw);
		if (index != null && index >= 0 && index < bookSize) {
			byte[] key = new byte[keySize];
			System.arraycopy(book, hintSize * bookSize + index * keySize, key, 0, keySize);
			return key;
		} else {
			return null;
		}
	}

	/**
	 * @return hint for today
	 */
	public byte[] getHint() {
		int index = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		return getHint(index);
	}

	private byte[] getHint(int index) {
		index = Math.abs(index);
		index %= bookSize;
		byte[] hint = new byte[hintSize];
		System.arraycopy(book, index * hintSize, hint, 0, hintSize);
		return hint;
	}

	public static byte[] genBook() {
		byte[] book = new byte[dataLength];
		Random random = new Random();
		boolean success = false;
		while (!success) {
			random.nextBytes(book);
			HintBook hb = new HintBook(book);
			if (hb.hint_index.size() == bookSize) {
				success = true;
			}
		}

		return book;
	}

	public static byte[] readBookFromImage(String path) {
		BufferedImage bookImg = null;
		try {
			bookImg = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Stega.decode(bookImg, HintBook.dataLength);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	// public static void main(String[] args) throws IOException {
	//
	// byte[] book = HintBook.genBook();
	// HintBook hb = new HintBook(book);
	//
	// byte[] hint = hb.getHint();
	// byte[] key = hb.getKey(hint);
	//
	// // Trace.trace(index);
	// Trace.trace(hint);
	// Trace.trace(key);
	//
	// System.exit(0);
	// }
}

final class ByteArrayWrapper {
	private final byte[] data;

	public ByteArrayWrapper(byte[] data) {
		if (data == null) {
			throw new NullPointerException();
		}
		this.data = data;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ByteArrayWrapper)) {
			return false;
		}
		return Arrays.equals(data, ((ByteArrayWrapper) other).data);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
}