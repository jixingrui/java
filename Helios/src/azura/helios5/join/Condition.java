package azura.helios5.join;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

public class Condition {

	public final Long head;
	SortedSet<Tuple2<Long, Long>> range;
	Iterator<Tuple2<Long, Long>> it;

	public Condition(Long head, NavigableSet<Tuple2<Long, Long>> set) {
		this.head = head;
		this.range = set;
	}

	public Long challenge(Long champion) {
		Tuple2<Long, Long> key = Fun.t2(head, champion);
		range = range.tailSet(key);
		it = range.iterator();
		if (!it.hasNext())
			return null;

		Tuple2<Long, Long> next = it.next();
		if (!next.a.equals(head))
			return null;

		return next.b;
	}

	Long next() {
		if (!it.hasNext()) {
			return null;
		} else {
			Tuple2<Long, Long> next = it.next();
			if (next.a.equals(head))
				return next.b;
			else
				return null;
		}
	}

}
