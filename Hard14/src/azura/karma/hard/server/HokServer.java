package azura.karma.hard.server;

import azura.fractale.netty.FrackServerA;
import azura.fractale.netty.handler.FrackUserA;
import azura.helios6.Helios6;
import azura.karma.def.KarmaSpace;
import azura.karma.hard.HardItem;

public class HokServer extends FrackServerA {

	private KarmaSpace ksHard;

	private HokUserI user;
	private Helios6<?> db;

	public HokServer(HokUserI user, Helios6<?> db) {
		this.user = user;
		this.db = db;
	}

	public HokServer readHardDef(String path) {
		ksHard = new KarmaSpace().fromFile(path);
		HardItem.ksHard = ksHard;
		return this;
	}

	@Override
	public FrackUserA newUser() {
		return new Connection(ksHard, user, db);
	}

}
