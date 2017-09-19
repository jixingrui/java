package azura.helios7.read;

import java.util.Iterator;
import java.util.NavigableSet;

public class Condition {

	public final long head;
	private NavigableSet<long[]> range;
	Iterator<long[]> it;

	public Condition(long head, NavigableSet<long[]> set) {
		this.head = head;
		this.range = set;
	}

	public Long challenge(long champion) {
		long[] key = new long[] { head, champion };
		it = range.tailSet(key, true).iterator();
		if (!it.hasNext())
			return null;

		long[] next = it.next();
		if (next[0] != head)
			return null;

		return next[1];
	}

	Long next() {
		if (!it.hasNext()) {
			return null;
		} else {
			long[] next = it.next();
			if (next[0] == head)
				return next[1];
			else
				return null;
		}
	}

}
