package zz.karma.Hard.PS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 删掉S.State.selectedItem
*<p>S.State.upList最后一个Item.numChildren减1
*<p>之后生成RefillPS触发它
*/
public class K_DeletePS extends KarmaReaderA {
	public static final int type = 18413740;

	public K_DeletePS(KarmaSpace space) {
		super(space, type , 18912937);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}