package zz.karma.JuniorEdit.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_TestRun extends KarmaReaderA {
	public static final int type = 65371461;

	public K_TestRun(KarmaSpace space) {
		super(space, type , 65371616);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}