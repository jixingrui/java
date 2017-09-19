package zz.karma.Hard.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_UnselectSC extends KarmaReaderA {
	public static final int type = 18410305;

	public K_UnselectSC(KarmaSpace space) {
		super(space, type , 18912919);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}