package azura.helios6.read;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.Iterators;

public class Roller {
	protected static final Logger log = Logger.getLogger(Roller.class);

	List<Condition> condList = new ArrayList<>();
	Iterator<Condition> iterator;
	Long champion = Long.MIN_VALUE;
	int stepCount = 0;

	public Roller(List<Condition> condList) {
		this.condList = condList;
		iterator = Iterators.cycle(condList);
	}

	public Long next() {

		if (champion == null)
			return null;

		while (stepCount < condList.size()) {

			// This is the reason why weight cannot be supported.
			// If weighted, then the result id won't be sorted any more. So the
			// next challenge cannot skip to the result id.
			// Without skip, the join operation will be much slower.
			Long local = iterator.next().challenge(champion);

			if (local == null) {
				return null;
			} else if (champion.equals(local)) {
				stepCount++;
			} else if (local > champion) {
				champion = local;
				stepCount = 1;
			} else {
				String message = "Roller: wrong order. champion=" + champion
						+ " local=" + local;
				log.fatal(message);
				return null;
			}
		}

		Long result = champion;
		champion = iterator.next().next();
		stepCount = 1;

		return result;
	}
}
