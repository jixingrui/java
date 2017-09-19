package common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SortedArrayList<E extends Comparable<E>> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(E paramE) {
		int insertionPoint = Collections.binarySearch(this, paramE);
		super.add((insertionPoint > -1) ? insertionPoint
				: (-insertionPoint) - 1, paramE);
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
}
