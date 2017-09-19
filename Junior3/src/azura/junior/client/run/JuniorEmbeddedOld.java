package azura.junior.client.run;

import org.apache.log4j.Logger;

import com.esotericsoftware.kryo.util.IntMap;

import azura.junior.client.JuniorInputI;
import azura.junior.client.JuniorOutputI;
import azura.junior.client.LogLevelE;
import azura.junior.client.ProA;
import azura.junior.engine.JuniorSpace;

public class JuniorEmbeddedOld implements JuniorInputI, JuniorOutputI {

	private static Logger log = Logger.getLogger(JuniorEmbeddedOld.class);

	private JuniorSpace space;
	private IntMap<ProA> id_Pro = new IntMap<>();
	// private JuniorOutputI output;

	public JuniorEmbeddedOld(JuniorSpace space) {
		this.space = space;
		// this.output = output;
	}

	@Override
	public void newEgo(ProA pro) {
		// pro.id = space.newEgo(pro.type, this).id;
		// id_Pro.put(pro.id, pro);
	}

	@Override
	public void newScene(ProA sPro) {
		// Scene scene = space.newScene(sPro.type, this);
		// sPro.id = scene.ego.id;
		// id_Pro.put(sPro.id, sPro);
		// scene.checkBoot();
	}

	@Override
	public void play(int idScene, int idxRole, int idEgo) {
		space.play(idScene, idxRole, idEgo);
	}

	@Override
	public void input(ProA host, int fact) {
		space.turnOnInput(host.id, fact);
	}

	@Override
	public void outLink(int idEgo, int idConcept, String name, String by) {
	}

	@Override
	public void output(int idEgo, int idConcept) {
		// log.debug("output:" + name);

		// ProA pro = id_Pro.get(idEgo);
		// pro.output(idConcept);
	}

	@Override
	public void ask(int idEgo, int idConcept, int idCycle) {
		// ProA pro = id_Pro.get(idEgo);
		// boolean answer = pro.ask(idConcept);
		// log.debug("answer:" + answer);
		// space.answer(idEgo, idConcept, answer, idCycle);
	}

	// @Override
	// public void log(int idEgo, String me, String by) {
	// log.debug("log:" + me + " " + by);
	// }

	@Override
	public void deleteEgo(int id) {
		// space.deleteEgo(id);
	}

	@Override
	public void deleteScene(int id) {
		// space.deleteScene(id);
	}

	@Override
	public void newEgoR(int token, int idEgo) {
	}

	@Override
	public void newSceneR(int token, int idScene) {
	}

	// @Override
	// public void log(int id, LogLevelE level, String name, String reason) {
	// }

	@Override
	public void log(LogLevelE level, int id, String name, String reason) {
	}

}
