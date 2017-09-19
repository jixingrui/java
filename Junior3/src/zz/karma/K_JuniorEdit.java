package zz.karma;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_JuniorEdit extends KarmaReaderA {
	public static final int type = 18383034;

	public K_JuniorEdit(KarmaSpace space) {
		super(space, type , 18913488);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}