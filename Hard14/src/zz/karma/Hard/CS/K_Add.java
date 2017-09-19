package zz.karma.Hard.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note if 上选中，加号是灰色
*<p>else 发送Add
*<p>
*/
public class K_Add extends KarmaReaderA {
	public static final int type = 18390071;

	public K_Add(KarmaSpace space) {
		super(space, type , 18912945);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}