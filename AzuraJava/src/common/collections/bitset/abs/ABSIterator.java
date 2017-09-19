package common.collections.bitset.abs;

import java.util.Iterator;

public class ABSIterator implements Iterator<Integer> {

	private Iterator<ABSData> it;

	int pos = -1;
	int forward = 0;

	public ABSIterator(Iterator<ABSData> iterator) {
		this.it = iterator;
	}

	@Override
	public boolean hasNext() {
		return forward > 0 || it.hasNext();
	}

	@Override
	public Integer next() {
		if (forward == 0) {
			ABSData start = it.next();
			pos = start.pos;
			forward = start.forward;
		} else {
			pos++;
			forward--;
		}
		return pos;
	}

	@Override
	public void remove() {
	}

}
