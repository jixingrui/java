package azura.helios6.read;

import java.util.Iterator;

import org.h2.mvstore.MVMap;

public class Condition {

	public final long head;
	private MVMap<long[], Boolean> range;
	Iterator<long[]> it;

	public Condition(long head, MVMap<long[], Boolean> set) {
		this.head = head;
		this.range = set;
	}

	public Long challenge(long champion) {
		long[] key = new long[] { head, champion };
		it = range.keyIterator(key);
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
