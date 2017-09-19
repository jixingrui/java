package azura.helios6.read;

import java.util.Iterator;

import org.h2.mvstore.MVMap;

public class Condition {

	public final Long head;
	private MVMap<Long[], Boolean> range;
	Iterator<Long[]> it;

	public Condition(Long head, MVMap<Long[], Boolean> set) {
		this.head = head;
		this.range = set;
	}

	public Long challenge(Long champion) {
		Long[] key = new Long[] { head, champion };
		it = range.keyIterator(key);
		if (!it.hasNext())
			return null;

		Long[] next = it.next();
		if (!next[0].equals(head))
			return null;

		return next[1];
	}

	Long next() {
		if (!it.hasNext()) {
			return null;
		} else {
			Long[] next = it.next();
			if (next[0].equals(head))
				return next[1];
			else
				return null;
		}
	}

}
