package zz.karma;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Ice extends KarmaReaderA {
	public static final int type = 93484272;

	public K_Ice(KarmaSpace space) {
		super(space, type , 93560463);
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