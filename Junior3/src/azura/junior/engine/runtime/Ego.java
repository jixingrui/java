package azura.junior.engine.runtime;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.esotericsoftware.kryo.util.IntMap;

import azura.junior.client.JuniorOutputI;
import azura.junior.client.LogLevelE;
import azura.junior.engine.def.Concept;
import azura.junior.engine.def.Idea;
import azura.junior.engine.def.Soul;
import azura.junior.reader.IoType;
import common.collections.bitset.SparseBitSet;

//the only true self of someone, disregarding the multiple roles he is acting as
public class Ego {
	private static final Logger log = Logger.getLogger(Ego.class);
	private static final int limit = 1;

	// type
	public Soul soul;

	// storage
	public int id;
	private SparseBitSet state = new SparseBitSet();
	public IntMap<Integer> concept_value = new IntMap<>();
	public ConcurrentLinkedQueue<Actor> playList = new ConcurrentLinkedQueue<>();

	// output
	JuniorOutputI listener;

	public Ego(int id, Soul soul, JuniorOutputI listener) {
		this.id = id;
		this.soul = soul;
		this.listener = listener;
	}

	// ============ creation ===========

	public ArrayList<Integer> trueList() {
		ArrayList<Integer> result = new ArrayList<>();
		for (int i = state.nextSetBit(0); i >= 0; i = state.nextSetBit(i + 1)) {
			result.add(i);
		}
		return result;
	}

	public void playAs(Actor actor) {
		playList.add(actor);
	}

	// ============ action =========
	public void turnOnInputTest(int idConcept) {
		Concept concept = soul.getConcept(idConcept);

		if (concept == null) {
			log.error("no such input: " + idConcept);
			return;
		}

		InputCycle cycle = new InputCycle(20);
		cycle.testMode = true;
		turnOn(concept, cycle, concept.name + "@" + soul.name, "<UnitTest>");
		cycle.cycle();
	}

	public void logInput(int idConcept) {
		Concept concept = soul.getConcept(idConcept);
		listener.log(LogLevelE.ALL, this.id, " ~(+) " + concept.name + "@" + soul.name, "<Enqueue>");
	}

	public void turnOnInput(int idConcept) {
		Concept concept = soul.getConcept(idConcept);

		if (concept == null || concept.ioType != IoType.INPUT) {
			log.error("no such input: " + idConcept);
			return;
		}

		InputCycle cycle = new InputCycle(limit);
		turnOn(concept, cycle, concept.name + "@" + soul.name, "<input>");
		cycle.cycle();
	}

	public void turnOn(Concept concept, InputCycle cycle, String me, String by) {
		if (playList == null)
			throw new Error();

		if (getState(concept) == true) {
			return;
		}

		IoType ioType = concept.ioType;
		if (ioType == IoType.EMPTY)
			return;

		if (ioType == IoType.OUTPUT)
			listener.log(LogLevelE.OUTPUT, this.id, " (+) " + me, by);
		else
			listener.log(LogLevelE.ALL, this.id, " (+) " + me, by);

		if (concept.counterTrigger > 0) {
			Integer old = concept_value.get(concept.id);
			if (old == null)
				old = 0;
			if (old < concept.counterTrigger) {
				old = concept.counterTrigger;
				concept_value.put(concept.id, old);
			}
			listener.log(LogLevelE.ALL, this.id, " () " + me, " = " + old + "/" + concept.counterTrigger);
		}

		state.set(concept.id);
		if (cycle.testMode) {
			Actor ta = playList.peek();
			Idea idea = ta.mind.getIdea(concept.id);
			if (idea.outLink) {
				listener.outLink(id, concept.id, me, by);
			}
		}
		if (ioType == IoType.OUTPUT) {
			listener.output(id, concept.id);
		}
		playList.forEach(actor -> {
			actor.turnOnAll(concept, cycle);
		});
		if (ioType == IoType.ASK) {
			cycle.ask();
			listener.ask(id, concept.id, cycle.id);
			if (cycle.testMode) {
				Actor ta = playList.peek();
				if (ta != null) {
					Idea idea = ta.mind.getIdea(concept.id);
					answer(concept.id, idea.defaultValue, cycle);
				}
			}
		}
	}

	public void answer(int idConcept, boolean value, InputCycle cycle) {
		if (soul.getConcept(idConcept).ioType != IoType.ASK) {
			log.error("no such ask");
			return;
		}

		Concept concept = soul.getConcept(idConcept);
		listener.log(LogLevelE.ALL, id, " () " + concept.name, "<answer " + value + ">");

		// InputCycle cycle = InputCycle.get(cycle);
		if (cycle == null)
			throw new Error();
		playList.forEach(actor -> {
			if (value == true)
				actor.turnOnTrigger(concept, cycle);
			else
				actor.turnOffTrigger(concept, cycle);
		});
		cycle.answer();
		// cycle.cycle();
	}

	// =============== off ==============
	public void turnOffInput(int concept) {
		log.error("======== disabled =========");
	}

	public void turnOff(Concept concept, InputCycle cycle, String me, String by) {
		turnOffC(concept, cycle, me, by);
		if (concept.counterTrigger > 0) {
			concept_value.remove(concept.id);
			listener.log(LogLevelE.ALL, this.id, " () " + concept.name + "@" + soul.name,
					" = 0" + "/" + concept.counterTrigger);
		}
	}

	public void turnOffC(Concept concept, InputCycle cycle, String me, String by) {
		if (getState(concept) == false) {
			return;
		}

		listener.log(LogLevelE.ALL, id, " (-) " + me, by);

		state.clear(concept.id);
		playList.forEach(actor -> {
			actor.turnOffAll(concept, cycle);
		});

	}

	// ============ value =========

	public void addValue(Concept concept, int value, InputCycle cycle, String me, String by) {
		Integer oldV = concept_value.get(concept.id);
		if (oldV == null)
			oldV = 0;

		int newV = oldV + value;
		newV = Math.max(0, newV);
		if (newV > 0)
			concept_value.put(concept.id, newV);
		else
			concept_value.remove(concept.id);

		listener.log(LogLevelE.ALL, id, " () " + me, " = " + newV + "/" + concept.counterTrigger);

		int trigger = concept.counterTrigger;
		if (oldV < trigger && newV >= trigger) {
			String b = by + " : " + newV + ">=" + trigger;
			turnOn(concept, cycle, me, b);
		} else if (oldV >= trigger && newV < trigger) {
			String b = by + " : " + newV + "<" + trigger;
			turnOffC(concept, cycle, me, b);
		}

	}

	// ================= support ==========

	public boolean getState(Concept concept) {
		if (state == null)
			throw new Error();
		return state.get(concept.id);
	}

	public void dispose() {
		soul = null;
		state = null;
		concept_value = null;
		playList = null;
	}

}
