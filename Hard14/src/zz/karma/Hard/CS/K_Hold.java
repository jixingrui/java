package zz.karma.Hard.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note if selected : hold，且把C.state.upList里最后一个Item赋值给C.state.heldItemMom
*<p>if unselected: unhold
*/
public class K_Hold extends KarmaReaderA {
	public static final int type = 18416126;

	public K_Hold(KarmaSpace space) {
		super(space, type , 18912957);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}