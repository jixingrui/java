package azura.junior.engine.runtime;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import azura.junior.client.JuniorOutputI;
import azura.junior.engine.JuniorSpace;
import azura.junior.engine.def.Idea;
import azura.junior.engine.def.Script;
import azura.junior.reader.IoType;

//each play
public class Scene {
	private static Logger log = Logger.getLogger(Scene.class);

	final JuniorSpace space;
	final Script script;
	public Actor[] actorList;
	public final Ego ego;
	public boolean testMode;

	public Scene(int id, Script script, JuniorSpace space, JuniorOutputI output) {
		this.script = script;
		this.space = space;
		this.ego = new Ego(id, script.soul, output);
		actorList = new Actor[script.mindList.length];
	}

	public Actor getActor(Idea task) {
		if (actorList == null)
			return null;
		// throw new Error();
		return actorList[task.mind.idx];
	}

	public void play(int idxActor, Ego ego) {
		if (actorList[idxActor] != null)
			throw new Error();

		Actor actor = new Actor(ego);
		actor.scene = this;
		actor.mind = script.mindList[idxActor];
		actorList[idxActor] = actor;

		if (idxActor > 0)
			checkBoot();
	}

	public void checkBoot() {
		for (int i = 1; i < actorList.length; i++) {
			if (actorList[i] == null)
				return;
		}
		play(0, ego);
		if (testMode) {
			initStateTest();
		} else {
			initActorState();
			turnOnDefault(actorList[0]);
		}
	}

	private void initStateTest() {
		for (int i = 1; i < actorList.length; i++) {
			Actor a = actorList[i];
			turnOnDefault(a);
		}
		Actor s = actorList[0];
		turnOnDefault(s);
	}

	private void turnOnDefault(Actor a) {
		a.mind.concept_Idea.values().forEach(idea -> {
			if (idea.defaultValue == true && idea.concept.ioType != IoType.ASK) {
				InputCycle cycle = new InputCycle(20);
				cycle.testMode = this.testMode;
				a.ego.turnOn(idea.concept, cycle, idea.logMe(), "<init>");
				cycle.cycle();
			}
		});
	}

	private void initActorState() {
		ArrayList<ArrayList<Integer>> trueState = new ArrayList<>(actorList.length - 1);
		for (int i = 1; i < actorList.length; i++) {
			Actor a = actorList[i];
			trueState.add(a.ego.trueList());
		}
		for (int i = 1; i < actorList.length; i++) {
			Actor a = actorList[i];
			ArrayList<Integer> state = trueState.get(i - 1);
			for (int idConcept : state) {
				Idea idea = a.mind.getIdea(idConcept);
				log.debug("init " + idea.concept.name);
				InputCycle cycle = new InputCycle(1);
				idea.turnOnTrigger(this, cycle);
				idea.turnOnCausal(this, cycle);
				cycle.cycle();
			}
		}
	}

	public boolean isDisposed() {
		return actorList == null;
	}

	public void dispose() {
		// actorList[0].dispose();
		for (int i = 0; i < actorList.length; i++) {
			Actor current = actorList[i];
			current.dispose();
			// current.ego.playList.remove(current);
		}
		// ego = null;
		actorList = null;
		// actorList[0].ego.dispose();
	}
}
