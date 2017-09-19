package zz.karma;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Ice4 extends KarmaReaderA {
	public static final int type = 113087402;

	public K_Ice4(KarmaSpace space) {
		super(space, type , 113099506);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}