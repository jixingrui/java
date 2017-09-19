package azura.ice.watch;

import java.util.HashSet;

import common.algorithm.FastMath;
import common.algorithm.Pack16;

public class Pair {
	Mover one, two;
	private HashSet<Watch> watchSet = new HashSet<>();

	public Pair(Mover host, Mover target) {
		one = (host.id < target.id) ? host : target;
		two = (host.id > target.id) ? host : target;
	}

	public void addWatch(Watch w) {
		watchSet.add(w);
	}

	public void removeWatch(Watch w) {
		watchSet.remove(w);
	}

	public void clear() {
		watchSet.clear();
	}

	public int getId() {
		return Pack16.pack(one.id, two.id);
	}

	public void notifyMove() {
		double dist = FastMath.dist(one.x, one.y, two.x, two.y);
		int distInt = (int) dist;
		watchSet.forEach((watch) -> {
			watch.move(distInt);
		});
	}

	public static int getId(Mover one, Mover two) {
		Mover small = (one.id < two.id) ? one : two;
		Mover large = (one.id > two.id) ? one : two;
		return Pack16.pack(small.id, large.id);
	}
}
