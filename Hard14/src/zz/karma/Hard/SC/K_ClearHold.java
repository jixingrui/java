package zz.karma.Hard.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_ClearHold extends KarmaReaderA {
	public static final int type = 18410861;

	public K_ClearHold(KarmaSpace space) {
		super(space, type , 18912925);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}