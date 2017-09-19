package azura.zombie.service;

import azura.fractale.netty.handler.FrackUserA;
import azura.karma.def.KarmaSpace;
import common.collections.buffer.i.ZintReaderI;

public class ZombieNet extends FrackUserA {

	private KarmaSpace ksZombie;
	// private KarmaSpace ksIce;
	private ZombieS_CS zombieS;

	public ZombieNet(KarmaSpace ksZombie, KarmaSpace ksIce) {
		this.ksZombie = ksZombie;
		// this.ksIce = ksIce;
		zombieS = new ZombieS_CS(ksZombie, ksIce, this);
	}

	@Override
	public void connected() {
		socketSend(ksZombie.toBytes());
		zombieS.initIce();
	}
	
	@Override
	public void disconnected() {
		zombieS.dispose();
	}

	@Override
	public void socketReceive(ZintReaderI reader) {
		zombieS.receive(reader);
	}

}
