package common.collections.bitset.lbs;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintReaderI;
import common.logger.Trace;

public class LBSet implements Iterable<Integer>, BytesI {
	private LinkedList<Integer> store = new LinkedList<Integer>();
	private boolean currentValue = false;
	private int writerIndex = -1;

	public LBSet() {
	}

	public LBSet(byte[] data) {
		fromBytes(data);
	}

	public LBSet(BitSet bs) {
		writerIndex = -2;
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			// operate on index i here
			if (writerIndex + 1 == i) {
				store.removeLast();
				store.add(i + 1);
			} else {
				store.add(i);
				store.add(i + 1);
			}
			writerIndex = i;
		}
	}

	public void push(boolean value) {
		writerIndex++;
		if (!value)
			;
		else if (currentValue) {
			store.removeLast();
			store.add(writerIndex + 1);
		} else {
			store.add(writerIndex);
			store.add(writerIndex + 1);
		}
		currentValue = value;
	}

	public void clear() {
		writerIndex = -1;
		currentValue = false;
		store.clear();
	}

	public Iterator<Integer> iterator() {
		return new LBSIterator(store.iterator());
	}

	public byte[] toBytes() {
		int prevPos = -1;
		ZintBuffer zb = new ZintBuffer();
		for (int value : store) {
			zb.writeZint(value - prevPos - 1);
			prevPos = value;
		}
		byte[] result = zb.toBytes();
		return result;
	}

	public void fromBytes(byte[] data) {
		store.clear();
		ZintReaderI zb = new ZintBuffer(data);
		int pointer = -1;
		while (zb.hasRemaining()) {
			int posTrue = pointer + zb.readZint() + 1;
			int posFalse = posTrue + zb.readZint() + 1;
			store.add(posTrue);
			store.add(posFalse);
			pointer = posFalse;
		}
	}

	public BitSet toBitSet() {
		BitSet bs = new BitSet();
		int from = -1;
		int to = -1;
		boolean pos = false;
		for (int i : store) {
			if (!pos) {
				from = i;
				pos = true;
			} else {
				to = i;
				bs.set(from, to);
				pos = false;
			}
		}
		return bs;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");

		Iterator<Integer> it = iterator();
		while (it.hasNext()) {
			sb.append(it.next() + " ,");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("}");
		return sb.toString();
	}

	// public static void main(String[] args) {
	// testIterator();
	// testBasic();
	// testBs();
	// }

	@SuppressWarnings("unused")
	private static void testBs() {
		BitSet bs = new BitSet();
		bs.set(0);
		bs.set(1);
		bs.set(10);
		bs.set(16);
		bs.set(25);
		LBSet lbs = new LBSet(bs);

		Trace.trace("lbs:\t" + lbs.toString());
	}

	@SuppressWarnings("unused")
	private static void testBasic() {
		LBSet lbs = new LBSet();
		lbs.push(false);
		lbs.push(false);
		lbs.push(false);
		lbs.push(true);
		lbs.push(true);
		lbs.push(false);
		lbs.push(false);
		lbs.push(true);
		lbs.push(true);
		lbs.push(true);
		lbs.push(true);
		lbs.push(true);
		lbs.push(false);
		lbs.push(true);
		lbs.push(false);
		Trace.trace("lbs:\t" + lbs.toString());

		BitSet bs = lbs.toBitSet();
		System.out.println(bs.toString());
	}

//	private static void testIterator() {
//		BitSet bs = new BitSet();
//		ABSet abs = new ABSet();
//		LBSet lbs = new LBSet();
//		for (int i = 0; i < 100; i++) {
//			int pos = (int) (Math.random() * Integer.MAX_VALUE % 100);
//			boolean value = (int) (Math.random() * Integer.MAX_VALUE % 2) == 0;
//			bs.set(pos, value);
//			abs.set(pos, value);
//		}
//
//		for (int i = 0; i < bs.length(); i++) {
//			lbs.push(bs.get(i));
//		}
//
//		Trace.trace("bs:\t" + bs.toString());
//		Trace.trace("abs:\t" + abs.toString());
//		Trace.trace("lbs:\t" + lbs.toString());
//
//		lbs.fromBytes(abs.toBytes());
//		Trace.trace("decode:\t" + lbs.toString());
//
//		lbs.fromBytes(lbs.toBytes());
//		Trace.trace("encode:\t" + lbs.toString());
//
//		lbs = new LBSet(bs);
//		Trace.trace("(load):\t" + lbs.toString());
//
//		bs = lbs.toBitSet();
//		Trace.trace("(toBitSet):\t" + lbs.toString());
//
//		// // Trace.trace(data);
//		//
//		// StringBuilder sb = new StringBuilder();
//		// sb.append("it:\t{");
//		// for (int i = 0; i < 100; i++) {
//		// if (abs.get(i))
//		// sb.append(i + " ,");
//		// }
//		// sb.delete(sb.length() - 2, sb.length());
//		// sb.append("}");
//		// Trace.trace(sb.toString());
//	}
}
