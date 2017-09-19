package zzz.karma._Hard._SC;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class RefillUp extends KarmaReaderA {
	public static final int type = 18410303;
	public static final int version = 18912917;

	public RefillUp(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type LIST
	*<p>[Item] empty
	*@note empty
	*/
	public static final int F_itemList = 0;

	public static final int T_Item = 18389685;
}