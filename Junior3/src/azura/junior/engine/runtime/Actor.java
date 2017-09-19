package azura.junior.engine.runtime;

import azura.junior.engine.def.Concept;
import azura.junior.engine.def.Idea;
import azura.junior.engine.def.Mind;
import azura.junior.reader.IoType;

//the virtual identity on someone when acting as a role
public class Actor {

	// type
	public Mind mind;
	// runtime
	public Scene scene;
	// storage
	public Ego ego;

	public Actor(Ego ego) {
		this.ego = ego;
		ego.playAs(this);
	}

	public void turnOnAll(Concept concept, InputCycle cycle) {
		Idea idea = mind.getIdea(concept.id);
		if (concept.ioType != IoType.ASK)
			idea.turnOnTrigger(scene, cycle);
		if (idea.flashy)
			cycle.addFirst(scene, idea.getTriggerFlashy(), "<flashy>");
		idea.turnOnCausal(scene, cycle);
	}

	public void turnOffAll(Concept concept, InputCycle cycle) {
		Idea idea = mind.getIdea(concept.id);
		if (concept.ioType != IoType.ASK)
			idea.turnOffTrigger(scene, cycle);
		idea.turnOffCausal(scene, cycle);
	}

	public void turnOnTrigger(Concept concept, InputCycle cycle) {
		Idea idea = mind.getIdea(concept.id);
		idea.turnOnTrigger(scene, cycle);
	}

	public void turnOffTrigger(Concept concept, InputCycle cycle) {
		Idea idea = mind.getIdea(concept.id);
		idea.turnOffTrigger(scene, cycle);
	}

	public void turnOnCausal(Concept concept, InputCycle cycle) {
		Idea idea = mind.getIdea(concept.id);
		idea.turnOnCausal(scene, cycle);
	}

	public void turnOffCausal(Concept concept, InputCycle cycle) {
		Idea idea = mind.getIdea(concept.id);
		idea.turnOffCausal(scene, cycle);
	}

	public void dispose() {
		ego.playList.remove(this);
		mind = null;
		scene = null;
		ego = null;
	}
}
