package azura.ice.service;

import azura.fractale.netty.handler.FrackUserA;
import azura.karma.def.KarmaSpace;
import azura.zombie.service.IceS_CS;
import common.collections.buffer.i.OutI;
import common.collections.buffer.i.ZintReaderI;

public class IceNet extends FrackUserA implements OutI {

	private IceS_CS s;
	private KarmaSpace ksIce;

	public IceNet(KarmaSpace ksIce) {
		this.ksIce = ksIce;
		s = new IceS_CS(ksIce, this);
	}

	@Override
	public void connected() {
		socketSend(ksIce.toBytes());
	}

	@Override
	public void socketReceive(ZintReaderI reader) {
		s.receive(reader.toBytes());
	}

}
