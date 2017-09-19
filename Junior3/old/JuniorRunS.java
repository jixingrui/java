package azura.junior.server;

import azura.junior.engine.JuniorDef;
import azura.junior.engine.JuniorOutputI;
import azura.junior.engine.JuniorSpace;
import azura.junior.engine.runtime.Ego;
import azura.junior.engine.runtime.Scene;
import azura.karma.def.KarmaSpace;
import azura.karma.run.Karma;
import common.collections.buffer.i.OutI;
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
import zz.karma.JuniorRun.SC.SCI.K_Output;
import zz.karma.JuniorRun.SC.SCI.K_log;

public class JuniorRunS extends K_CS implements JuniorOutputI {

	JuniorSpace js;
	private OutI out;

	public JuniorRunS(KarmaSpace space, JuniorDef def, OutI out) {
		super(space);
		this.js = new JuniorSpace(def);
		this.out = out;
	}

	// ================= input ================
	public void receive(ZintReaderI reader) {
		this.fromBytes(reader.toBytes());
		Karma msg = send;
		if (msg.getType() == T_Mark) {
		} else if (msg.getType() == T_NewEgo) {
			K_NewEgo ne = new K_NewEgo(space);
			ne.fromKarma(msg);
			newEgo(ne);
		} else if (msg.getType() == T_DeleteEgo) {
			K_DeleteEgo de = new K_DeleteEgo(space);
			de.fromKarma(msg);
			js.deleteEgo(de.id);
		} else if (msg.getType() == T_NewScene) {
			K_NewScene ns = new K_NewScene(space);
			ns.fromKarma(msg);
			newScene(ns);
		} else if (msg.getType() == T_DeleteScene) {
			K_DeleteScene ds = new K_DeleteScene(space);
			ds.fromKarma(msg);
			js.deleteScene(ds.id);
		} else if (msg.getType() == T_Play) {
			K_Play play = new K_Play(space);
			play.fromKarma(msg);
			Scene scene = js.getScene(play.idScene);
			Ego ego = js.getEgo(play.idEgo);
			scene.play(play.idxRole, ego);
		} else if (msg.getType() == T_CSI) {
			K_CSI cs = new K_CSI(space);
			cs.fromKarma(msg);
			receiveCsi(cs);
		}

	}

	private void receiveCsi(K_CSI cs) {
		int who = cs.idEgo;
		Karma msg = cs.msg;
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
			log.error("AddValue: not implemented");
		}
	}

	public void sendSci(int who, Karma msg) {
		K_SCI sci = new K_SCI(space);
		sci.idEgo = who;
		sci.msg = msg;
		send(sci.toKarma());
	}

	public void send(Karma msg) {
		K_SC sc = new K_SC(space);
		sc.send = msg;
		out.out(sc.toBytes());
	}

	@Override
	public void output(int ego, int concept, String name, String by) {
		K_Output output = new K_Output(space);
		output.concept = concept;
		sendSci(ego, output.toKarma());
	}

	@Override
	public void ask(int ego, int concept, int idCycle) {
		K_Ask ask = new K_Ask(space);
		ask.concept = concept;
		ask.idCycle = idCycle;
		sendSci(ego, ask.toKarma());
	}

	@Override
	public void log(int ego, String me, String by) {
		K_log log = new K_log(space);
		// log.soul = me;
		log.concept = me;
		log.note = by;
		// log.us = us;
		sendSci(ego, log.toKarma());
	}

	@Override
	public void outLink(int ego, int concept, String name, String by) {
	}

	private void newEgo(K_NewEgo ne) {

		Ego ego = js.newEgo(ne.identity, this, ne.token);
		if (ego == null) {
			log.warn("no such identity: " + ne.identity);
			return;
		}
	}

	@Override
	public void newEgoR(int token, int idEgo) {
		K_NewEgoR ret = new K_NewEgoR(space);
		ret.id = idEgo;
		ret.token = token;
		send(ret.toKarma());
	}

	private void newScene(K_NewScene ns) {
		js.newScene(ns.script, this, ns.token);
	}

	@Override
	public void newSceneR(int token, int idScene) {
		K_NewSceneR ret = new K_NewSceneR(space);
		ret.id = idScene;
		ret.token = token;
		send(ret.toKarma());
	}

}
