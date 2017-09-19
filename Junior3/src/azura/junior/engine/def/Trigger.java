package azura.junior.engine.def;

import azura.junior.engine.runtime.Actor;
import azura.junior.engine.runtime.InputCycle;
import azura.junior.engine.runtime.Scene;

//the prepared responce of one act
public class Trigger {

	public Idea target;
	public int value;

	public void trigger(Scene scene, InputCycle cycle, String reason) {
		Actor actor = scene.getActor(target);
		if (actor == null)
			return;

		if (target.concept.counterTrigger > 0) {
			actor.ego.addValue(target.concept, value, cycle, target.concept.name + "@" + target.mind.name, reason);
		} else {
			if (value > 0)
				actor.ego.turnOn(target.concept, cycle, target.concept.name + "@" + target.mind.name, reason);
			else if (value < 0) {
				actor.ego.turnOff(target.concept, cycle, target.concept.name + "@" + target.mind.name, reason);
			}
		}
	}

	public Trigger clone() {
		Trigger result = new Trigger();
		result.target = target;
		result.value = value;
		return result;
	}
}
