package zzz.karma._Hard._CS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note if selected : hold，且把C.state.upList里最后一个Item赋值给C.state.heldItemMom
*
*if unselected: unhold
*/
public abstract class Hold extends KarmaReaderA {
	public static final int type = 18416126;
	public static final int version = 18912957;

	public Hold(KarmaSpace space) {
		super(space, type , version);
	}


}