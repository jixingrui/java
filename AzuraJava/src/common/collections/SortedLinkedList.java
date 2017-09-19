package common.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class SortedLinkedList<E extends Comparable<E>> extends LinkedList<E> {

	private static final long serialVersionUID = 1L;

	public boolean addSort(E paramE) {
		int insertionPoint = Collections.binarySearch(this, paramE);
		super.add((insertionPoint > -1) ? insertionPoint : (-insertionPoint) - 1, paramE);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> paramCollection) {
		boolean result = false;
		for (E paramT : paramCollection) {
			result |= add(paramT);
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		forEach(item -> {
			sb.append(item).append(",");
		});
		return sb.toString();
	}
}
