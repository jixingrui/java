package zz.karma.Hard.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note depends on selected
*<p>
*/
public class K_Jump extends KarmaReaderA {
	public static final int type = 18416130;

	public K_Jump(KarmaSpace space) {
		super(space, type , 18912961);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}