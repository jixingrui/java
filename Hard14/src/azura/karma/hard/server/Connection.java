package azura.karma.hard.server;

import azura.expresso.rpc.phoenix13.TunnelI;
import azura.fractale.netty.handler.FrackUserA;
import azura.helios6.Helios6;
import azura.karma.def.KarmaSpace;
import azura.karma.hard.HubScExt;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;

public class Connection extends FrackUserA implements TunnelI {

	private HubScExt hub;
	private byte[] ksHardData;

	public Connection(KarmaSpace ksHard, HokUserI user, Helios6<?> db) {
		this.ksHardData = ksHard.toBytes();
		hub = new HubScExt(ksHard, this, db, user);
	}

	@Override
	public void connected() {
		log.info("send hard def");
		ZintBuffer zb = new ZintBuffer(ksHardData);
		tunnelOut(zb);
		hub.connected();
	}

	@Override
	public void disconnected() {
	}

	@Override
	public void socketReceive(ZintReaderI reader) {
		hub.receive(reader.toBytes());
	}

	@Override
	public void tunnelOut(ZintReaderI reader) {
		socketSend(reader);
	}

}
