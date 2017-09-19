package azura.junior.engine.runtime;

import azura.junior.engine.def.Trigger;

public class Next {
	public Scene scene;
	public Trigger trigger;
	public final String reason;

	public Next(String reason) {
		this.reason = reason;
	}

	public void fire(InputCycle cycle) {
		trigger.trigger(scene, cycle, reason);
	}
}
