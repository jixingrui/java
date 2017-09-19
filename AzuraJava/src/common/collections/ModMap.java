package common.collections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ModMap<K, V> {
	private HashMap<K, V> mainMap = new HashMap<>();

	// loop
	private final AtomicBoolean inLoop = new AtomicBoolean(false);
	private LinkedList<V> loopQue = new LinkedList<>();

	public void put(K k, V v) {
		mainMap.put(k, v);
		if (inLoop.get() == true) {
			loopQue.addLast(v);
		}
	}

	public V get(K k) {
		return mainMap.get(k);
	}

	public void remove(K k) {
		V v = mainMap.remove(k);
		if (inLoop.get() == true) {
			loopQue.remove(v);
		}
	}

	public void forEachValue(Consumer<? super V> action) {
		inLoop.set(true);
		loopQue.addAll(mainMap.values());
		while (loopQue.isEmpty() == false) {
			V current = loopQue.removeFirst();
			action.accept(current);
		}
		inLoop.set(false);
	}

	public boolean isEmpty() {
		return mainMap.isEmpty();
	}

	public void clear() {
		mainMap.clear();
	}

}
