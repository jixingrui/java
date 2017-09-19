package azura.ice.watch;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import common.algorithm.FastMath;
import common.algorithm.Pack16;

public class Pair implements Comparable<Pair> {
	protected static Logger log = Logger.getLogger(Pair.class);

	int oldDist;
	private Watcher one, two;
	private ConcurrentLinkedQueue<Watch> watchList = new ConcurrentLinkedQueue<>();

	public Pair(Watcher host, Watcher target) {
		one = (host.id < target.id) ? host : target;
		two = (host.id > target.id) ? host : target;
	}

	public int getId() {
		return Pack16.pack(one.id, two.id);
	}

	public void addWatch(Watch w) {
		watchList.add(w);
	}

	public void removeWatch(Watch w) {
		if (watchList == null)
			return;

		watchList.remove(w);
		if (watchList.isEmpty()) {
			int pid = getId();
			one.id_Pair.remove(pid);
			two.id_Pair.remove(pid);
		}
	}

	public void notifyMove() {
		double distD = FastMath.dist(one.x, one.y, two.x, two.y);
		// Logger.getLogger(getClass()).debug("watcher pair " + getId() + "
		// dist=" + distD);
		int newDist = (int) distD;
		if (oldDist == newDist)
			return;
//		log.debug("dist from " + oldDist + " to " + newDist);
//		if(Math.abs(oldDist-newDist)>100)
//			log.error("jump!!!");
		oldDist = newDist;
		watchList.forEach((watch) -> {
			watch.check(newDist);
		});
	}

	@Override
	public int compareTo(Pair other) {
		if (this.oldDist > other.oldDist)
			return 1;
		else if (this.oldDist < other.oldDist)
			return -1;
		else
			return 0;
	}

	@Override
	public boolean equals(Object obj) {
		Pair other = (Pair) obj;
		if (other == null)
			return false;
		else
			return this.one == other.one && this.two == other.two;
	}

	public void dispose() {
		one = null;
		two = null;
		watchList = null;
	}

	public void removeTarget(Watcher target) {
		Iterator<Watch> it = watchList.iterator();
		while (it.hasNext()) {
			if (it.next().target == target) {
				it.remove();
			}
		}
	}

	public boolean isEmpty() {
		return watchList.isEmpty();
	}
}
