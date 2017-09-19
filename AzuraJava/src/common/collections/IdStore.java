package common.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

/**
 * thread safe
 */
public class IdStore<V extends IdStoreI> implements Iterable<V>, BytesI {

	protected static Logger log = Logger.getLogger(IdStore.class);

	private int recycleSize;
	private int dataSize;
	final ConcurrentLinkedQueue<Integer> recycle = new ConcurrentLinkedQueue<Integer>();
	final List<V> store = Collections.synchronizedList(new ArrayList<V>());

	public IdStore(int recycleSize) {
		this.recycleSize = recycleSize;
	}

	public int size() {
		return dataSize;
	}

	public synchronized void add(V item) {
		dataSize++;
		Integer id;
		if (recycle.size() > recycleSize) {
			id = recycle.poll();
			store.set(id, item);
		} else {
			id = store.size();
			store.add(item);
		}
		item.setId(id);
	}

	public synchronized V remove(int id) {
		if (id < 0 || id >= store.size())
			throw new Error();

		dataSize--;
		V result = store.set(id, null);
		recycle.add(id);
		return result;
	}

	public V get(int id) {
		if (id >= 0 && id < store.size()) {
			return store.get(id);
		} else {
			log.warn("id not found");
			return null;
		}
	}

	public void set(V v) {
		if (v.getId() >= 0 && v.getId() < store.size()) {
			store.set(v.getId(), v);
		} else {
			log.warn("id not found");
		}
	}

	public void clear() {
		recycle.clear();
		store.clear();
	}

	/*
	 * value is null for recycled elements
	 */
	@Override
	public Iterator<V> iterator() {
		return store.iterator();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		recycleSize = zb.readZint();
		recycle.clear();
		for (int i = 0; i < recycleSize; i++) {
			recycle.add(zb.readZint());
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(recycleSize);
		for (Integer r : recycle) {
			zb.writeZint(r);
		}
		return zb.toBytes();
	}

}
