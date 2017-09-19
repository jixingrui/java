package zz.karma.Hard.PS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 把S.State.downList清空
*<p>ClearDown->SC.send,发送出去
*/
public class K_ClearDownPS extends KarmaReaderA {
	public static final int type = 18413736;

	public K_ClearDownPS(KarmaSpace space) {
		super(space, type , 18912933);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}