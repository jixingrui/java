package zz.karma.Hard;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Hub extends KarmaReaderA {
	public static final int type = 18389692;

	public K_Hub(KarmaSpace space) {
		super(space, type , 18912899);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}