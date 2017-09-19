package azura.junior.server;

import azura.fractale.netty.handler.FrackUserA;
import azura.junior.client.run.JuniorS;
import azura.junior.client.run.JuniorTunnelI;
import azura.junior.engine.JuniorSpace;
import azura.karma.def.KarmaSpace;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;
import zz.karma.JuniorRun.K_CS;
import zz.karma.JuniorRun.K_SC;

public class JuniorRunNet extends FrackUserA implements JuniorTunnelI {

	public JuniorS cs;
	private KarmaSpace space;

	public JuniorRunNet(KarmaSpace space, JuniorSpace js) {
		this.space = space;
		cs = new JuniorS(space, js, this);
	}

	@Override
	public void connected() {
		out(space.toBytes());
	}

	@Override
	public void disconnected() {
	}

	@Override
	public void socketReceive(ZintReaderI reader) {
		// log.debug("socket receive");
		cs.receive(reader);
	}

	@Override
	public void out(byte[] data) {
		ZintBuffer zb = new ZintBuffer(data);
		socketSend(zb);
	}

	@Override
	public void sendCS(K_CS cs) {
	}

	@Override
	public void sendSC(K_SC sc) {
		out(sc.toBytes());
	}

}
