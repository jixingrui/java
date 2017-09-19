package azura.junior.client.run;

import org.apache.log4j.Logger;

import azura.junior.client.JuniorOutputI;
import azura.junior.client.LogLevelE;
import azura.junior.engine.JuniorSpace;
import azura.junior.engine.runtime.Ego;
import azura.junior.engine.runtime.Scene;
import azura.karma.def.KarmaSpace;
import azura.karma.run.Karma;
import common.collections.buffer.i.ZintReaderI;
import zz.karma.JuniorRun.K_CS;
import zz.karma.JuniorRun.K_SC;
import zz.karma.JuniorRun.CS.K_CSI;
import zz.karma.JuniorRun.CS.K_DeleteEgo;
import zz.karma.JuniorRun.CS.K_DeleteScene;
import zz.karma.JuniorRun.CS.K_NewEgo;
import zz.karma.JuniorRun.CS.K_NewScene;
import zz.karma.JuniorRun.CS.K_Play;
import zz.karma.JuniorRun.CS.CSI.K_AskR;
import zz.karma.JuniorRun.CS.CSI.K_TurnOff;
import zz.karma.JuniorRun.CS.CSI.K_TurnOn;
import zz.karma.JuniorRun.SC.K_NewEgoR;
import zz.karma.JuniorRun.SC.K_NewSceneR;
import zz.karma.JuniorRun.SC.K_SCI;
import zz.karma.JuniorRun.SC.SCI.K_Ask;
import zz.karma.JuniorRun.SC.SCI.K_Log;
import zz.karma.JuniorRun.SC.SCI.K_Output;

public class JuniorS implements JuniorOutputI {
	private static final Logger log = Logger.getLogger(JuniorS.class);

	private KarmaSpace space;
	JuniorSpace js;
	private JuniorTunnelI tunnel;

	public JuniorS(KarmaSpace ksJuniorRun, JuniorSpace js, JuniorTunnelI tunnel) {
		this.space = ksJuniorRun;
		this.js = js;
		this.tunnel = tunnel;
	}

	// ================= input ================
	public void receive(ZintReaderI reader) {
		K_CS cs = new K_CS(space);
		cs.fromBytes(reader.toBytes());
		receive(cs);
	}

	public void receive(final K_CS cs) {
		logInput(cs);
		// log.info("plan: " + System.identityHashCode(cs));
		js.addTask(() -> {
			// log.info("run: " + System.identityHashCode(cs));
			process(cs);
		});
	}

	private void process(K_CS cs) {
		Karma msg = cs.send;
		if (msg.getType() == K_CS.T_Mark) {
		} else if (msg.getType() == K_CS.T_NewEgo) {
			K_NewEgo ne = new K_NewEgo(space);
			ne.fromKarma(msg);
			newEgo(ne);
		} else if (msg.getType() == K_CS.T_DeleteEgo) {
			K_DeleteEgo de = new K_DeleteEgo(space);
			de.fromKarma(msg);
			js.deleteEgo(de.id, this);
		} else if (msg.getType() == K_CS.T_NewScene) {
			K_NewScene ns = new K_NewScene(space);
			ns.fromKarma(msg);
			newScene(ns);
		} else if (msg.getType() == K_CS.T_DeleteScene) {
			K_DeleteScene ds = new K_DeleteScene(space);
			ds.fromKarma(msg);
			js.deleteScene(ds.id, this);
		} else if (msg.getType() == K_CS.T_Play) {
			K_Play play = new K_Play(space);
			play.fromKarma(msg);
			Scene scene = js.getScene(play.idScene);
			Ego idEgo = js.getEgo(play.idEgo);
			scene.play(play.idxRole, idEgo);
		} else if (msg.getType() == K_CS.T_CSI) {
			K_CSI csi = new K_CSI(space);
			csi.fromKarma(msg);
			process(csi);
		}
	}

	private void process(K_CSI csi) {
		int who = csi.idEgo;
		Karma msg = csi.msg;
		if (msg.getType() == K_CSI.T_TurnOn) {
			K_TurnOn in = new K_TurnOn(space);
			in.fromKarma(msg);
			js.turnOnInput(who, in.concept);
		} else if (msg.getType() == K_CSI.T_TurnOff) {
			K_TurnOff in = new K_TurnOff(space);
			in.fromKarma(msg);
			js.turnOffInput(who, in.concept);
		} else if (msg.getType() == K_CSI.T_AskR) {
			K_AskR in = new K_AskR(space);
			in.fromKarma(msg);
			js.answer(who, in.concept, in.value, in.idCycle);
		} else if (msg.getType() == K_CSI.T_AddValue) {
			// log.error("AddValue: not implemented");
		}
	}

	private void logInput(K_CS cs) {
		Karma msg = cs.send;
		if (msg.getType() == K_CS.T_CSI) {
			K_CSI csi = new K_CSI(space);
			csi.fromKarma(msg);
			logInput(csi);
		}
	}

	private void logInput(K_CSI csi) {
		int who = csi.idEgo;
		Karma msg = csi.msg;
		if (msg.getType() == K_CSI.T_TurnOn) {
			K_TurnOn in = new K_TurnOn(space);
			in.fromKarma(msg);
			js.logInput(who, in.concept);
		}
	}

	private void newEgo(K_NewEgo ne) {
		js.newEgo(ne.identity, this, ne.token);
	}

	private void newScene(K_NewScene ns) {
		js.newScene(ns.script, this, ns.token);
	}

	// ============== send =============
	public void sendSci(int who, Karma msg) {
		K_SCI sci = new K_SCI(space);
		sci.idEgo = who;
		sci.msg = msg;
		send(sci.toKarma());
	}

	public void send(Karma msg) {
		K_SC sc = new K_SC(space);
		sc.send = msg;
		// out.out(sc.toBytes());
		tunnel.sendSC(sc);
	}

	@Override
	public void output(int idEgo, int idConcept) {
		K_Output output = new K_Output(space);
		output.concept = idConcept;
		sendSci(idEgo, output.toKarma());
	}

	@Override
	public void ask(int idEgo, int idConcept, int idCycle) {
		K_Ask ask = new K_Ask(space);
		ask.concept = idConcept;
		ask.idCycle = idCycle;
		sendSci(idEgo, ask.toKarma());
	}

	@Override
	public void outLink(int idEgo, int idConcept, String name, String by) {
	}

	@Override
	public void newEgoR(int token, int idEgo) {
		K_NewEgoR ret = new K_NewEgoR(space);
		ret.id = idEgo;
		ret.token = token;
		send(ret.toKarma());
	}

	@Override
	public void newSceneR(int token, int idScene) {
		K_NewSceneR ret = new K_NewSceneR(space);
		ret.id = idScene;
		ret.token = token;
		send(ret.toKarma());
	}

	@Override
	public void log(LogLevelE level, int id, String name, String reason) {
		K_Log log = new K_Log(space);
		log.name = name;
		log.note = reason;
		sendSci(id, log.toKarma());
	}

}
