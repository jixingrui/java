package azura.junior.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import azura.junior.client.run.JuniorCSI;
import azura.junior.client.run.JuniorTunnelI;
import common.collections.IdRecycle;
import zz.karma.JuniorRun.K_CS;

public class JuniorRelay implements JuniorInputI {
	private static Logger log = Logger.getLogger(JuniorRelay.class);

	private JuniorCSI cs;
	private JuniorTunnelI tunnel;

	private Map<Integer, ProA> id_ProA = new ConcurrentHashMap<Integer, ProA>();

	// exchange
	private IdRecycle ir = new IdRecycle(1);
	private Map<Integer, ProA> token_ProA = new ConcurrentHashMap<Integer, ProA>();

	public JuniorRelay(JuniorCSI cs, JuniorTunnelI tunnel) {
		this.cs = cs;
		this.tunnel = tunnel;
	}

	public void newEgo(ProA ego) {
		int token = ir.nextId();
		token_ProA.put(token, ego);
		cs.newEgo(ego.type, token);
	}

	public void newEgoR(int token, int id) {
		ProA ego = token_ProA.remove(token);
		ego.id = id;
		id_ProA.put(id, ego);
		ir.recycle(token);
		// log.info("J(" + id + "): creation complete");
		ego.initFuture.complete(null);
	}

	public void newScene(ProA scene) {
		int token = ir.nextId();
		token_ProA.put(token, scene);
		cs.newScene(scene.type, token);
	}

	public void newSceneR(int token, int id) {
		ProA scene = token_ProA.remove(token);
		scene.id = id;
		id_ProA.put(id, scene);
		ir.recycle(token);
		// log.info("J(" + id + "): creation complete");
		scene.initFuture.complete(null);
	}

	@Override
	public void play(int idScene, int idxRole, int idEgo) {
		cs.play(idScene, idxRole, idEgo);
	}

	public void ask(int idEgo, int concept, int idCycle) {
		ProA target = id_ProA.get(idEgo);
		if (target == null) {
			log.warn("ask target not exist: (" + idEgo + ") " + concept);
			return;
		}
		boolean value = target.ask(concept);
		cs.answer(idEgo, concept, value, idCycle);
	}

	public void output(int idEgo, int concept) {
		ProA target = id_ProA.get(idEgo);
		if (target == null) {
			log.warn("output target not exist: (" + idEgo + ") " + concept);
			return;
		}
		target.output(concept);
	}

	public void log(int idEgo, String name, String note) {
		ProA target = id_ProA.get(idEgo);
		if (target == null) {
			log.warn("Dead J(" + idEgo + "): " + name + " " + note);
			return;
		}
		target.log(name, note);
	}

	public void input(ProA host, int fact) {
		// StackTraceElement[] stacks = new Throwable().getStackTrace();
		// String mName = stacks[1].getMethodName();
		// log.debug("J:(排队)" + mName);
		if (id_ProA.containsKey(host.id))
			cs.turnOn(host.id, fact);
		else
			log.warn("J(" + host.id + "): already dead and cannot input");
	}

	public void sendCS(K_CS cs) {
		tunnel.sendCS(cs);
	}

	@Override
	public void deleteEgo(int id) {
		id_ProA.remove(id);
		cs.deleteEgo(id);
	}

	@Override
	public void deleteScene(int id) {
		id_ProA.remove(id);
		cs.deleteScene(id);
	}

}
