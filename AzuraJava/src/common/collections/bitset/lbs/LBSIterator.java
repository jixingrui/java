package common.collections.bitset.lbs;

import java.util.Iterator;

public class LBSIterator implements Iterator<Integer> {

	private Iterator<Integer> it;

	int currentPos = 0;
	int currentGoal = 0;

	public LBSIterator(Iterator<Integer> iterator) {
		this.it = iterator;
	}

	@Override
	public boolean hasNext() {
		return currentPos != currentGoal || it.hasNext();
	}

	@Override
	public Integer next() {
		if (currentPos == currentGoal) {
			currentPos = it.next();
			currentGoal = it.next();
		}
		return currentPos++;
	}

	@Override
	public void remove() {
	}

}
