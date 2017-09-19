package azura.junior.engine.action;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import azura.junior.engine.Trigger;

import com.esotericsoftware.kryo.util.ObjectMap;

public class CopyOfInputCycle {
	private static final Logger log = Logger.getLogger(CopyOfInputCycle.class);

	private final int limit;
	LinkedList<Next> triggerQue;
	
	//numeric
	ObjectMap<Trigger, Integer> Trigger_Count;

	public CopyOfInputCycle(int limit) {
		this.limit = limit;
	}

	public void add(Scene scene, Trigger trigger) {
		if (overflow(trigger)) {
			log.error("trigger overflow");
			return;
		}

		Next t = new Next();
		t.scene = scene;
		t.trigger = trigger;
		triggerQue.add(t);
	}

	private boolean overflow(Trigger trigger) {
		Integer count = Trigger_Count.get(trigger);
		if (count == null) {
			Trigger_Count.put(trigger, 1);
			return false;
		}

		if (count <= limit) {
			Trigger_Count.put(trigger, count + 1);
			return false;
		} else {
			return true;
		}
	}

	public void cycle() {
		while (triggerQue.isEmpty() == false) {
			Next next = triggerQue.removeFirst();
			next.fire(this);
		}
	}
}
