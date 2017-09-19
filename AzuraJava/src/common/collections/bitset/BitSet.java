package common.collections.bitset;

import java.util.Arrays;

public class BitSet {

	// public static void main(String[] args) {
	// BitSet bs = new BitSet();
	// Trace.trace(bs.toString());
	// bs.set(3);
	// Trace.trace(bs.toString());
	// bs.set(8);
	// Trace.trace(bs.toString());
	// bs.set(6, 18);
	// Trace.trace(bs.toString());
	// }

	/*
	 * BitSets are packed into arrays of "words." Currently a word is a long,
	 * which consists of 64 bits, requiring 6 address bits. The choice of word
	 * size is determined purely by performance concerns.
	 */
	private final static int ADDRESS_BITS_PER_WORD = 3;
	private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;

	/* Used to shift left or right for a partial word mask */
	private static final int WORD_MASK = 0xffffffff;

	/**
	 * The internal field corresponding to the serialField "bits".
	 */
	private int[] words;

	/**
	 * The number of words in the logical size of this BitSet.
	 */
	private int wordsInUse = 0;

	private int maxSetBit = -1;

	/**
	 * Given a bit index, return word index containing it.
	 */
	private static int wordIndex(int bitIndex) {
		return bitIndex >> ADDRESS_BITS_PER_WORD;
	}

	/**
	 * Creates a new bit set. All bits are initially {@code false}.
	 */
	public BitSet() {
		initWords(BITS_PER_WORD);
	}

	public BitSet(int nbits) {
		// nbits can't be negative; size 0 is OK
		if (nbits < 0)
			throw new Error("nbits < 0: " + nbits);

		initWords(nbits);
	}

	private void initWords(int nbits) {
		words = new int[wordIndex(nbits - 1) + 1];
	}

	private void ensureCapacity(int wordsRequired) {
		if (words.length < wordsRequired) {
			// Allocate larger of doubled size or required size
			int request = Math.max(2 * words.length, wordsRequired);
			words = Arrays.copyOf(words, request);
		}
	}

	private void expandTo(int wordIndex) {
		int wordsRequired = wordIndex + 1;
		if (wordsInUse < wordsRequired) {
			ensureCapacity(wordsRequired);
			wordsInUse = wordsRequired;
		}
	}

	private static void checkRange(int fromIndex, int toIndex) {
		if (fromIndex < 0)
			throw new Error("fromIndex < 0: " + fromIndex);
		if (toIndex < 0)
			throw new Error("toIndex < 0: " + toIndex);
		if (fromIndex > toIndex)
			throw new Error("fromIndex: " + fromIndex + " > toIndex: "
					+ toIndex);
	}

	public boolean get(int bitIndex) {
		if (bitIndex < 0)
			throw new Error("bitIndex < 0: " + bitIndex);

		int wordIndex = wordIndex(bitIndex);
		return (wordIndex < wordsInUse)
				&& ((words[wordIndex] & (1L << bitIndex)) != 0);
	}

	public void set(int bitIndex) {
		if (bitIndex < 0)
			throw new Error("bitIndex < 0: " + bitIndex);

		int wordIndex = wordIndex(bitIndex);
		expandTo(wordIndex);

		words[wordIndex] |= (1L << bitIndex); // Restores invariants

		maxSetBit = Math.max(maxSetBit, bitIndex);
	}

	public void set(int fromIndex, int toIndex) {
		checkRange(fromIndex, toIndex);

		if (fromIndex == toIndex)
			return;

		// Increase capacity if necessary
		int startWordIndex = wordIndex(fromIndex);
		int endWordIndex = wordIndex(toIndex - 1);
		expandTo(endWordIndex);

		long firstWordMask = WORD_MASK << fromIndex;
		long lastWordMask = WORD_MASK >>> -toIndex;
		if (startWordIndex == endWordIndex) {
			// Case 1: One word
			words[startWordIndex] |= (firstWordMask & lastWordMask);
		} else {
			// Case 2: Multiple words
			// Handle first word
			words[startWordIndex] |= firstWordMask;

			// Handle intermediate words, if any
			for (int i = startWordIndex + 1; i < endWordIndex; i++)
				words[i] = WORD_MASK;

			// Handle last word (restores invariants)
			words[endWordIndex] |= lastWordMask;
		}

		maxSetBit = Math.max(maxSetBit, toIndex - 1);
	}

	public void clear(int bitIndex) {
		if (bitIndex < 0)
			throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);

		int wordIndex = wordIndex(bitIndex);
		if (wordIndex >= wordsInUse)
			return;

		words[wordIndex] &= ~(1L << bitIndex);

		// ==============@todo: update maxSetBit===========
	}

	public String toString() {

		StringBuilder b = new StringBuilder();
		b.append('{');

		for (int i = 0; i <= maxSetBit; i++) {
			if (get(i)) {
				b.append(i).append(",");
			}
		}
		if(b.lastIndexOf(",")==b.length()-1)
			b.deleteCharAt(b.length()-1);
		b.append('}');
		return b.toString();
	}
}
