package common.collections;

import java.util.TreeSet;

import org.apache.log4j.Logger;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

/**
 * thread safe
 */
public class IdRecycle implements BytesI {

	protected static Logger log = Logger.getLogger(IdRecycle.class);

	private volatile int next;
	private volatile int keep;
	private final TreeSet<Integer> recycle = new TreeSet<>();

	public IdRecycle() {
		this(0);
	}

	public IdRecycle(int start) {
		this(start, 0);
	}

	public IdRecycle(int start, int keep) {
		this.next = start;
		this.keep = (keep >= 0) ? keep : 0;
	}

	synchronized public int nextId() {
		Integer id;
		if (recycle.size() > keep) {
			id = recycle.pollFirst();
		} else {
			id = next;
			next++;
		}
		return id;
	}

	synchronized public void recycle(int id) {
		if (id >= next)
			throw new Error();
		boolean success = recycle.add(id);
		if (success == false)
			throw new Error();
		if (recycle.size() > keep) {
			while (recycle.isEmpty() == false) {
				int top = recycle.last();
				if (top == next - 1) {
					recycle.remove(top);
					next--;
				} else {
					break;
				}
			}
		}
	}

	// private void check() {
	// if (recycle.isEmpty() == false && next < recycle.last())
	// throw new Error();
	// }

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer reader = new ZintBuffer(bytes);
		next = reader.readZint();
		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			recycle.add(reader.readZint());
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer writer = new ZintBuffer();
		writer.writeZint(next);
		writer.writeZint(recycle.size());
		for (Integer r : recycle) {
			writer.writeZint(r);
		}
		return writer.toBytes();
	}

}
