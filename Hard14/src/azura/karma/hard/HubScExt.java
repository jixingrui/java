package azura.karma.hard;

import azura.expresso.rpc.phoenix13.TunnelI;
import azura.helios6.Helios6;
import azura.karma.def.KarmaSpace;
import azura.karma.hard.server.HokUserI;
import azura.karma.run.Karma;
import common.collections.ArrayListAuto;
import common.collections.buffer.ZintBuffer;
import zz.karma.Hard.Hub.K_CustomMsg;
import zz.karma.Hard.Hub.K_HubCS;
import zz.karma.Hard.Hub.K_HubSC;
import zz.karma.Hard.Hub.HubCS.K_HardMsgCS;
import zz.karma.Hard.Hub.HubSC.K_HardMsgSC;

public class HubScExt extends K_HubCS implements HardTunnelI {

	// private static Logger log = Logger.getLogger(HubScExt.class);

	private TunnelI tunnel;

	ArrayListAuto<HardS_CS> hardList = new ArrayListAuto<HardS_CS>();

	private Helios6<?> db;

	private HokUserI user;

	public HubScExt(KarmaSpace ksHard, TunnelI tunnel, Helios6<?> db, HokUserI user) {
		super(ksHard);
		this.tunnel = tunnel;
		this.db = db;
		this.user = user;

	}

	public void register(Enum<?> idx, HardHandlerI user) {
		HardS_CS hard = new HardS_CS(space, idx.ordinal(), this, db, user);
		hardList.set(idx.ordinal(), hard);
	}

	public HardS_CS get(Enum<?> idx) {
		return hardList.get(idx.ordinal());
	}

	public void connected() {
		user.initHard(this);
	}

	// =============== receive ==============
	public void receive(byte[] bytes) {
		fromBytes(bytes);
		// karma.fromBytes(bytes);
		// Karma msg = karma.getKarma(F_send);

		if (send.getType() == T_CustomMsg) {
			receiveCustom(send);
		} else if (send.getType() == T_HardMsgCS) {
			receiveHard(send);
		}

	}

	private void receiveHard(Karma msg) {
		K_HardMsgCS k = new K_HardMsgCS(space);
		k.fromKarma(msg);
		// int idx = msg.getInt(HardMsgCS.F_idxHard);
		// Karma sc = msg.getKarma(HardMsgCS.F_msgCS);

		HardS_CS target = hardList.get(k.idxHard);
		target.receive(k.msgCS);
	}

	private void receiveCustom(Karma msg) {
		K_CustomMsg k = new K_CustomMsg(space);
		k.fromKarma(msg);
		// byte[] cargo = msg.getBytes(CustomMsg.F_data);
		user.receiveCustom(k.data);
		// send to custom handler
	}

	// ===================== send ===============

	public void sendCustom(byte[] cargo) {
		// Karma msg = new Karma(space).fromType(CustomMsg.type);
		K_CustomMsg k = new K_CustomMsg(space);
		k.data = cargo;
		// msg.setBytes(CustomMsg.F_data, cargo);
		send(k.toKarma());
	}

	@Override
	public void sendHard(int idx, Karma cargo) {
		// Karma msg = new Karma(space).fromType(HardMsgSC.type);
		K_HardMsgSC k = new K_HardMsgSC(space);
		k.idxHard = idx;
		k.msgSC.fromKarma(cargo);
		// msg.setInt(HardMsgSC.F_idxHard, idx);
		// msg.setKarma(HardMsgSC.F_msgSC, cargo);
		send(k.toKarma());
	}

	public void send(Karma msg) {
		K_HubSC k = new K_HubSC(space);
		k.send = msg;
		// Karma sc = new Karma(space).fromType(HubSC.type);
		// sc.setKarma(F_send, msg);
		// byte[] data = sc.toBytes();
		ZintBuffer zb = new ZintBuffer(k.toBytes());
		tunnel.tunnelOut(zb);
	}

	// =================== send end =============
	public void reloadAll() {
		for (HardS_CS hard : hardList) {
			hard.coder.reloadAll();
		}
	}

//	@Override
//	public void fromKarma(Karma karma) {
//	}
//
//	@Override
//	public Karma toKarma() {
//		return null;
//	}

}
