package zzz.karma._Hard._PS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 删掉S.State.selectedItem
*S.State.upList最后一个Item.numChildren减1
*之后生成RefillPS触发它
*/
public abstract class DeletePS extends KarmaReaderA {
	public static final int type = 18413740;
	public static final int version = 18912937;

	public DeletePS(KarmaSpace space) {
		super(space, type , version);
	}


}