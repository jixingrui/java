package zz.karma.Hard;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_PS extends KarmaReaderA {
	public static final int type = 18389855;

	public K_PS(KarmaSpace space) {
		super(space, type , 18912903);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}