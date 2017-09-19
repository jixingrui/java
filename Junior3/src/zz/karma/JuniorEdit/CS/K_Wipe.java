package zz.karma.JuniorEdit.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Wipe extends KarmaReaderA {
	public static final int type = 18384280;

	public K_Wipe(KarmaSpace space) {
		super(space, type , 18913506);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}