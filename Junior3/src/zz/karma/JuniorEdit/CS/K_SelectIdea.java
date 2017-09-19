package zz.karma.JuniorEdit.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SelectIdea extends KarmaReaderA {
	public static final int type = 70158352;

	public K_SelectIdea(KarmaSpace space) {
		super(space, type , 70158943);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}