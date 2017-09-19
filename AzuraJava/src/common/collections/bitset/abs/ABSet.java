package common.collections.bitset.abs;

import java.util.Iterator;

import common.collections.aa.AATree;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintReaderI;

public class ABSet implements Iterable<Integer>, BytesI {

	private AATree<ABSData> tree = new AATree<ABSData>();

	public ABSet() {
	}

	public ABSet(byte[] data) {
		fromBytes(data);
	}

	public void set(int pos, boolean value) {
		if (pos < 0)
			throw new IllegalArgumentException();

		ABSData found = tree.findSelfOrSmaller(new ABSData(pos));

		if (found != null) {
			boolean old = pos <= found.pos + found.forward;
			if (value && !old) {
				ABSData after = tree.findSelfOrSmaller(new ABSData(pos + 1));
				boolean beforeConnected = found.pos + found.forward + 1 == pos;
				boolean afterConnected = found != after;

				if (beforeConnected && afterConnected) {
					found.forward = after.pos + after.forward - found.pos;
					tree.remove(new ABSData(pos + 1));
				} else if (beforeConnected) {
					found.forward++;
				} else if (afterConnected) {
					tree.remove(new ABSData(pos + 1));
					ABSData item = new ABSData(pos);
					item.forward = after.forward + 1;
					tree.insert(item);
				} else {
					tree.insert(new ABSData(pos));
				}

			} else if (!value && old) {
				boolean beforeEmpty = found.pos == pos;
				boolean afterEmpty = found.pos + found.forward == pos;
				if (beforeEmpty && afterEmpty) {
					tree.remove(new ABSData(pos));
				} else if (beforeEmpty) {
					tree.remove(new ABSData(pos));
					ABSData item = new ABSData(pos + 1);
					item.forward = found.forward - 1;
					tree.insert(item);
				} else if (afterEmpty) {
					found.forward--;
				} else {
					ABSData item = new ABSData(pos + 1);
					item.forward = found.pos + found.forward - pos - 1;
					tree.insert(item);

					found.forward = pos - found.pos - 1;
				}
			}
		} else if (value == false) {
			return;
		} else if (value == true) {
			ABSData after = tree.findSelfOrSmaller(new ABSData(pos + 1));
			if (after == null) {
				tree.insert(new ABSData(pos));
			} else {
				tree.remove(new ABSData(pos + 1));
				ABSData item = new ABSData(pos);
				item.forward = after.forward + 1;
				tree.insert(item);
			}
		}
	}

	public boolean get(int pos) {
		if (pos < 0)
			throw new IllegalArgumentException();

		ABSData found = tree.findSelfOrSmaller(new ABSData(pos));

		if (found == null)
			return false;
		else
			return pos <= found.pos + found.forward;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new ABSIterator(tree.iterator());
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		int pointer = -1;
		Iterator<ABSData> it = tree.iterator();
		while (it.hasNext()) {
			ABSData item = it.next();
			zb.writeZint(item.pos - pointer - 1);
			zb.writeZint(item.forward);
			pointer = item.pos + item.forward + 1;
		}
		return zb.toBytes();
	}

	public void fromBytes(byte[] data) {
		tree.clear();
		ZintReaderI zb = new ZintBuffer(data);
		int pointer = -1;
		while (zb.hasRemaining()) {
			int pos = zb.readZint() + pointer + 1;
			ABSData entry = new ABSData(pos);
			entry.forward = zb.readZint();
			tree.insert(entry);
			pointer = entry.pos + entry.forward + 1;
		}
	}

//	public static void main(String[] args) {
//		testBasic();
//		testWild();
//
//	}

//	private static void testWild() {
//		BitSet bs = new BitSet();
//		ABSet abs = new ABSet();
//		for (int i = 0; i < 100; i++) {
//			int pos = (int) (Math.random() * Integer.MAX_VALUE % 100);
//			boolean value = (int) (Math.random() * Integer.MAX_VALUE % 2) == 0;
//			bs.set(pos, value);
//			abs.set(pos, value);
//		}
//
//		Trace.trace("bs:\t" + bs.toString());
//		Trace.trace("abs:\t" + abs.toString());
//
//		byte[] data = abs.toBytes();
//		abs.fromBytes(data);
//		Trace.trace("reload:\t" + abs.toString());
//		// Trace.trace(data);
//
//		StringBuilder sb = new StringBuilder();
//		sb.append("it:\t{");
//		for (int i = 0; i < 100; i++) {
//			if (abs.get(i))
//				sb.append(i + " ,");
//		}
//		sb.delete(sb.length() - 2, sb.length());
//		sb.append("}");
//		Trace.trace(sb.toString());
//	}
//
//	private static void testBasic() {
//		ABSet abs = new ABSet();
//		abs.set(1, true);
//		abs.set(2, true);
//		abs.set(3, true);
//		abs.set(6, true);
//		abs.set(7, true);
//		abs.set(10, true);
//		abs.set(11, true);
//		abs.set(12, true);
//		abs.set(13, true);
//		abs.set(14, true);
//
//		Trace.trace(abs.toString());
//
//		abs.set(5, true);
//
//		Trace.trace(abs.toString());
//	}

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

}