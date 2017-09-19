package azura.junior.engine;

import org.apache.log4j.Logger;

import com.esotericsoftware.kryo.util.IntMap;

import azura.junior.client.JuniorOutputI;
import azura.junior.client.LogLevelE;
import azura.junior.engine.def.JuniorDef;
import azura.junior.engine.def.Script;
import azura.junior.engine.def.Soul;
import azura.junior.engine.runtime.Ego;
import azura.junior.engine.runtime.InputCycle;
import azura.junior.engine.runtime.Scene;
import common.collections.IdRecycle;
import common.thread.WorkerThread;

//the stage
public class JuniorSpace {
	private static final Logger log = Logger.getLogger(JuniorSpace.class);

	private final JuniorDef def;
	private final WorkerThread worker;

	// ======== data ==========
	IdRecycle idBank = new IdRecycle(1, 10);
	private IntMap<Ego> id_Ego = new IntMap<>();
	IntMap<Scene> id_Scene = new IntMap<>();

	public JuniorSpace(JuniorDef def) {
		this.def = def;
		worker = new WorkerThread("JuniorSpace");
		// worker.start();
	}

	public Ego newEgo(int profession, JuniorOutputI output, int token) {
		Soul soul = def.profession_Soul.get(profession);
		if (soul == null)
			return null;
		Ego ego = new Ego(idBank.nextId(), soul, output);
		id_Ego.put(ego.id, ego);
		output.newEgoR(token, ego.id);
		output.log(LogLevelE.ALL, ego.id, " () " + soul.name, "<生成>");
		return ego;
	}

	public Scene newScene(int idScript, JuniorOutputI output, int token) {
		Script script = def.id_Script.get(idScript);
		if (script == null)
			return null;
		Scene scene = new Scene(idBank.nextId(), script, this, output);
		id_Scene.put(scene.ego.id, scene);
		id_Ego.put(scene.ego.id, scene.ego);
		output.newSceneR(token, scene.ego.id);
		output.log(LogLevelE.ALL, scene.ego.id, " () " + script.name, "<生成>");
		scene.checkBoot();
		return scene;
	}

	public void play(int idScene, int idxActor, int idEgo) {
		Scene scene = id_Scene.get(idScene);
		Ego ego = id_Ego.get(idEgo);
		scene.play(idxActor, ego);
	}

	public void deleteEgo(int idEgo, JuniorOutputI output) {
		Ego ego = id_Ego.get(idEgo);
		if (ego.playList != null) {
			log.error("!!!canot delete playing ego!!! " + ego.soul.name);
			return;
		}
		id_Ego.remove(idEgo);
		idBank.recycle(idEgo);
		output.log(LogLevelE.ALL, ego.id, " () " + ego.soul.name, "<销毁>");
		ego.dispose();
	}

	public void deleteScene(int idScene, JuniorOutputI output) {
		Scene scene = id_Scene.get(idScene);
		if (scene == null) {
			log.error("Error: no such scene!!!");
			return;
		}
		id_Ego.remove(idScene);
		id_Scene.remove(idScene);
		idBank.recycle(idScene);
		output.log(LogLevelE.ALL, scene.ego.id, " () " + scene.ego.soul.name, "<销毁>");
		scene.dispose();
	}

	// =========== input =============

	public void logInput(int idEgo, int idConcept) {
		Ego who = id_Ego.get(idEgo);
		if (who == null) {
			log.error("no such ego");
			return;
		}
		who.logInput(idConcept);
	}

	public synchronized void turnOnInput(int ego, int concept) {
//		int uid = FastMath.randomBrightColor();
//		log.info("input start: ======== " + uid + " ========");
		Ego who = id_Ego.get(ego);
		if (who == null) {
			log.error("no such ego");
			return;
		}
		who.turnOnInput(concept);
//		log.info("input end:  ======== " + uid + " ========");
	}

	public synchronized void turnOffInput(int ego, int concept) {
		Ego who = id_Ego.get(ego);
		if (who == null) {
			log.error("no such ego");
			return;
		}
		who.turnOffInput(concept);
	}

	public void answer(int ego, int concept, boolean value, int idCycle) {
		Ego who = id_Ego.get(ego);
		if (who == null) {
			log.error("no such ego");
			return;
		}
		InputCycle cycle = InputCycle.get(idCycle);
		who.answer(concept, value, cycle);
		cycle.cycle();
	}

	// ======== support =========
	public Scene getScene(int idScene) {
		return id_Scene.get(idScene);
	}

	public Ego getEgo(int idEgo) {
		return id_Ego.get(idEgo);
	}

	public void dispose() {
		worker.kill();
	}

	public void addTask(Runnable task) {
		worker.plan(task);
	}

}
