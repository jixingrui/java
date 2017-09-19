package azura.ice.watch;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import common.collections.CompareE;

public class Watcher {
	protected static Logger log = Logger.getLogger(Watcher.class);
	ConcurrentHashMap<Integer, Pair> id_Pair = new ConcurrentHashMap<>();
	public int id;
	public int x, y;

	public Watch addWatch(Watcher target, CompareE condition, int trigger, Runnable callBack) {
		Watch watch = new Watch();
		watch.host = this;
		watch.target = target;
		watch.condition = condition;
		watch.triggerValue = trigger;
		watch.callBack = callBack;

		Pair p = new Pair(this, target);
		int id = p.getId();
		Pair old = id_Pair.get(id);
		if (old == null) {
			id_Pair.put(id, p);
			target.id_Pair.put(id, p);
			p.notifyMove();
		} else {
			p = old;
			watch.check(p.oldDist);
		}

		p.addWatch(watch);
		watch.pair = p;
		return watch;
	}

	public void removeTarget(Watcher other) {
		int id = new Pair(this, other).getId();
		Pair old = id_Pair.get(id);
		if (old == null) {
			log.error("nothing to remove");
			return;
		}
		old.removeTarget(other);
		if (old.isEmpty()) {
			id_Pair.remove(id);
			other.id_Pair.remove(id);
		}
	}

	public void watcherMoved(int x, int y) {
		this.x = x;
		this.y = y;
		id_Pair.forEach((id, pair) -> {
			pair.notifyMove();
		});
	}

}
