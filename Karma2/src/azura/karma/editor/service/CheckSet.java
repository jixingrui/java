package azura.karma.editor.service;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class CheckSet<T> implements Iterable<T> {

	private LinkedList<T> list = new LinkedList<T>();
	private LinkedHashSet<T> set = new LinkedHashSet<T>();

	public void addAll(List<T> more) {
		for (T one : more) {
			add(one);
		}
	}

	private boolean add(T one) {
		if (set.contains(one))
			return false;

		list.addLast(one);
		set.add(one);
		return true;
	}

	public boolean allChecked() {
		return list.isEmpty();
	}

	public T removeFirst() {
		return list.removeFirst();
	}

	@Override
	public Iterator<T> iterator() {
		return set.iterator();
	}

}
