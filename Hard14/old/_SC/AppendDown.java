package zzz.karma._Hard._SC;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class AppendDown extends KarmaReaderA {
	public static final int type = 18390013;
	public static final int version = 18912913;

	public AppendDown(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type LIST
	*<p>[Item] empty
	*@note empty
	*/
	public static final int F_itemList = 0;
	/**
	*@type BOOLEAN
	*@note empty
	*/
	public static final int F_end = 1;

	public static final int T_Item = 18389685;
}