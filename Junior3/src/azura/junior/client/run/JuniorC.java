package azura.junior.client.run;

import org.apache.log4j.Logger;

import azura.junior.client.JuniorRelay;
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

public class JuniorC extends K_SC implements JuniorCSI {

	private static Logger log = Logger.getLogger(JuniorC.class);

	public JuniorRelay relay;

	public JuniorC(KarmaSpace space, JuniorTunnelI tunnel) {
		super(space);
		relay = new JuniorRelay(this, tunnel);
	}

	// ==================== receive =================
	public void receive(ZintReaderI reader) {
		this.fromBytes(reader.toBytes());
		receive(this);
	}

	public void receive(K_SC sc) {
		Karma msg = sc.send;
		if (msg.getType() == K_SC.T_Mark) {

		} else if (msg.getType() == K_SC.T_NewEgoR) {
			K_NewEgoR in = new K_NewEgoR(space);
			in.fromKarma(msg);
			relay.newEgoR(in.token, in.id);
		} else if (msg.getType() == K_SC.T_NewSceneR) {
			K_NewSceneR in = new K_NewSceneR(space);
			in.fromKarma(msg);
			relay.newSceneR(in.token, in.id);
		} else if (msg.getType() == K_SC.T_SCI) {
			K_SCI sci = new K_SCI(space);
			sci.fromKarma(msg);
			receiveSci(sci);
		}
	}

	private void receiveSci(K_SCI sci) {
		Karma msg = sci.msg;

		if (msg.getType() == K_Output.type) {
			K_Output out = new K_Output(space);
			out.fromKarma(msg);
			relay.output(sci.idEgo, out.concept);
		} else if (msg.getType() == K_Ask.type) {
			K_Ask ask = new K_Ask(space);
			ask.fromKarma(msg);
			relay.ask(sci.idEgo, ask.concept, ask.idCycle);
		} else if (msg.getType() == K_Log.type) {
			K_Log kl = new K_Log(space);
			kl.fromKarma(msg);
			relay.log(sci.idEgo, kl.name, kl.note);
			// relay.log(kl.)
			// log.debug("J: " + kl.name + " " + kl.note);
		}
	}

	// =============== action =================
	@Override
	public void newEgo(int identity, int token) {
		K_NewEgo ne = new K_NewEgo(space);
		ne.identity = identity;
		ne.token = token;
		send(ne.toKarma());
	}

	@Override
	public void newScene(int script, int token) {
		K_NewScene ns = new K_NewScene(space);
		ns.script = script;
		ns.token = token;
		send(ns.toKarma());
	}

	@Override
	public void deleteEgo(int id) {
		K_DeleteEgo de = new K_DeleteEgo(space);
		de.id = id;
		send(de.toKarma());
	}

	@Override
	public void deleteScene(int id) {
		K_DeleteScene ds = new K_DeleteScene(space);
		ds.id = id;
		send(ds.toKarma());
	}

	@Override
	public void play(int idScene, int idxRole, int idEgo) {
		if(idScene==0 || idxRole==0 || idEgo==0)
			throw new Error();
		
		K_Play msg = new K_Play(space);
		msg.idScene = idScene;
		msg.idxRole = idxRole;
		msg.idEgo = idEgo;
		send(msg.toKarma());
	}

	@Override
	public void turnOn(int role, int fact) {
		K_TurnOn msg = new K_TurnOn(space);
		msg.concept = fact;
		sendCsi(role, msg.toKarma());
	}

	@Override
	public void turnOff(int role, int fact) {
		K_TurnOff msg = new K_TurnOff(space);
		msg.concept = fact;
		sendCsi(role, msg.toKarma());
	}

	@Override
	public void answer(int idEgo, int concept, boolean value, int idCycle) {
		K_AskR askr = new K_AskR(space);
		askr.concept = concept;
		askr.value = value;
		askr.idCycle = idCycle;
		sendCsi(idEgo, askr.toKarma());
	}

	// =============== send ============
	private void sendCsi(int idEgo, Karma msg) {
		K_CSI csi = new K_CSI(space);
		csi.idEgo = idEgo;
		csi.msg = msg;
		send(csi.toKarma());
	}

	private void send(Karma msg) {
		K_CS cs = new K_CS(space);
		cs.send = msg;
		relay.sendCS(cs);
		// this.send = msg;
		// client.send(this.toBytes());
	}

	// @Override
	// public void log(int ego, String soul, String concept) {
	// log.debug(soul + ":" + concept);
	// K_log kl=new K_log(space);
	// kl.concept=concept;
	// kl.soul=soul;
	// sendCsi(ego, kl.toKarma());
	// }

}
