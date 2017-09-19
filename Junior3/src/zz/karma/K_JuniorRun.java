package zz.karma;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_JuniorRun extends KarmaReaderA {
	public static final int type = 18828733;

	public K_JuniorRun(KarmaSpace space) {
		super(space, type , 18913685);
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