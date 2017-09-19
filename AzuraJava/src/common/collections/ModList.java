package common.collections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ModList<T> {
	private ArrayList<T> mainList = new ArrayList<>();

	// loop
	private final AtomicBoolean inLoop = new AtomicBoolean(false);
	private LinkedList<T> loopQue = new LinkedList<>();

	public void add(T item) {
		mainList.add(item);
		if (inLoop.get() == true) {
			loopQue.addLast(item);
		}
	}

	public void remove(T item) {
		mainList.remove(item);
		if (inLoop.get() == true) {
			loopQue.remove(item);
		}
	}

	public void forEach(Consumer<? super T> action) {
		inLoop.set(true);
		loopQue.addAll(mainList);
		while (loopQue.isEmpty() == false) {
			T current = loopQue.removeFirst();
			action.accept(current);
		}
		inLoop.set(false);
	}

	public boolean isEmpty() {
		return mainList.isEmpty();
	}

	public void clear() {
		mainList.clear();
	}

	public T get(int i) {
		return mainList.get(0);
	}
}
