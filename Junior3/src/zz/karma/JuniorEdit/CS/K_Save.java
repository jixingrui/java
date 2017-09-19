package zz.karma.JuniorEdit.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Save extends KarmaReaderA {
	public static final int type = 18384277;

	public K_Save(KarmaSpace space) {
		super(space, type , 18913504);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}