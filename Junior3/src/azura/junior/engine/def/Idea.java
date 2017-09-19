package azura.junior.engine.def;

import azura.junior.engine.runtime.Actor;
import azura.junior.engine.runtime.InputCycle;
import azura.junior.engine.runtime.Scene;

//each action the actor plays
public class Idea {

	public boolean defaultValue;
	public boolean flashy;
	public boolean outLink;
	private Trigger triggerFlashy;

	// concept
	public Concept concept;
	public Mind mind;

	// relation
	public Idea[] causeBy;
	public Idea[] causeTo;
	public Trigger[] triggerOn;
	public Trigger[] triggerOff;

	public Idea(Concept concept, Mind role, boolean initValue) {
		this.concept = concept;
		this.mind = role;
		this.defaultValue = initValue;
	}

	public Trigger getTriggerFlashy() {
		return triggerFlashy.clone();
	}

	public void setTriggerFlashy(Trigger trigger) {
		this.triggerFlashy = trigger;
	}

	public void turnOnTrigger(Scene scene, InputCycle cycle) {
		cycle.add(scene, triggerOn, "<< " + logMe());
	}

	public void turnOnCausal(Scene scene, InputCycle cycle) {
		for (Idea ct : causeTo) {
			ct.causalMerge(scene, cycle);
		}
		for (Idea cb : causeBy) {
			Actor target = scene.getActor(cb);
			target.ego.turnOn(cb.concept, cycle, cb.logMe(), "=>" + logMe());
		}
	}

	public void turnOffTrigger(Scene scene, InputCycle cycle) {
		cycle.add(scene, triggerOff, "<< " + logMe());
	}

	public void turnOffCausal(Scene scene, InputCycle cycle) {
		for (Idea ct : causeTo) {
			Actor target = scene.getActor(ct);
			target.ego.turnOff(ct.concept, cycle, ct.logMe(), "<= " + logMe());
		}
		boolean allOn = true;
		for (Idea cb : causeBy) {
			Actor target = scene.getActor(cb);
			if (target.ego.getState(cb.concept) == false) {
				allOn = false;
				continue;
			}
		}
		if (allOn == false)
			return;

		for (Idea cb : causeBy) {
			Actor target = scene.getActor(cb);
			target.ego.turnOff(cb.concept, cycle, cb.logMe(), "=> " + logMe(target));
		}
	}

	private void causalMerge(Scene scene, InputCycle cycle) {
		boolean allOn = true;
		StringBuilder sb = new StringBuilder();
		sb.append("<= ");
		sb.append("[");
		for (Idea cb : causeBy) {
			if (cb == this)
				continue;

			Actor target = scene.getActor(cb);
			boolean state = target.ego.getState(cb.concept);
			if (state == false) {
				allOn = false;
				break;
			}

			sb.append(cb.logMe(target)).append(" & ");
		}
		sb.delete(sb.length() - 3, sb.length());
		sb.append("]");
		if (allOn) {
			Actor me = scene.getActor(this);
			me.ego.turnOn(this.concept, cycle, logMe(), sb.toString());
		}
	}

	public String logMe() {
		return concept.name + "@" + mind.name;
	}

	String logMe(Actor actor) {
		return concept.name + "@" + mind.name + "(" + actor.ego.id + ")";
	}

}
