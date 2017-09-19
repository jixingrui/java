package azura.junior.engine.runtime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.esotericsoftware.kryo.util.IntMap;

import azura.junior.engine.def.Trigger;
import common.collections.IdRecycle;

public class InputCycle {
	private static final Logger log = Logger.getLogger(InputCycle.class);

	private static final IdRecycle idRecycle = new IdRecycle(1);
	private static final IntMap<InputCycle> id_Cycle = new IntMap<>();

	private final int limit;
	LinkedList<Next> triggerQue = new LinkedList<>();

	HashMap<Tuple, Integer> Trigger_Count = new HashMap<>();

	class Tuple {

		private Scene scene;
		private Trigger trigger;

		public Tuple(Scene scene, Trigger trigger) {
			this.scene = scene;
			this.trigger = trigger;
		}

		@Override
		public int hashCode() {
			return Objects.hash(scene, trigger);
		}

		@Override
		public boolean equals(Object obj) {
			Tuple other = (Tuple) obj;
			if (other == null)
				return false;
			else
				return this.scene == other.scene && this.trigger == other.trigger;
		}
	}

	public final long start;

	public final int id;

	private int asking = 0;

	public boolean testMode;

	public InputCycle(int limit) {
		this.limit = limit;
		this.start = System.nanoTime();
		// tid = FastMath.tidInt();
		id = idRecycle.nextId();
		id_Cycle.put(id, this);
	}

	public void dispose() {
		id_Cycle.remove(id);
		idRecycle.recycle(id);
		triggerQue = null;
		Trigger_Count = null;
	}

	public int timeUs() {
		return (int) ((System.nanoTime() - start) / 1000);
	}

	public void add(Scene scene, Trigger[] list, String reason) {
		// for (int i = 0; i < list.length; i++) {
		// addOne(scene, list[i], reason);
		// }
		for (int i = list.length - 1; i >= 0; i--) {
			addFirst(scene, list[i], reason);
		}
	}

	public void addFirst(Scene scene, Trigger trigger, String reason) {

		// if (trigger.target.concept.name.equals("死亡"))
		// log.warn("死亡" + "by" + reason);

		if (overflow(scene, trigger)) {
			log.error("!!!trigger overflow!!! : " + trigger.target.concept.name + reason);
			// log.error("========== 因果已断开 ===========");
			return;
		}

		if (scene.isDisposed())
			throw new Error();

		Next t = new Next(reason);
		t.scene = scene;
		t.trigger = trigger;
		triggerQue.addFirst(t);
	}

	private boolean overflow(Scene scene, Trigger trigger) {
		Tuple key = new Tuple(scene, trigger);

		Integer count = Trigger_Count.get(trigger);
		if (count == null) {
			Trigger_Count.put(key, 1);
			return false;
		}

		if (count < limit) {
			Trigger_Count.put(key, count + 1);
			return false;
		} else {
			return true;
		}
	}

	public void cycle() {
		while (true) {
			if (asking > 0)
				break;
			if (triggerQue.isEmpty())
				break;
			Next next = triggerQue.removeFirst();
			next.fire(this);
		}
		if (asking == 0 && triggerQue.isEmpty()) {
			// id_Cycle.remove(this.tid);
			dispose();
		}
	}

	public static InputCycle get(int idCycle) {
		return id_Cycle.get(idCycle);
	}

	public void ask() {
		// if (asking == 0 && id_Cycle.containsKey(tid)==false) {
		// id_Cycle.put(tid, this);
		// }
		asking++;
	}

	public void answer() {
		asking--;
	}

}
