package zzz.karma._Hard._SC;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class SelectSC extends KarmaReaderA {
	public static final int type = 18410307;
	public static final int version = 18912921;

	public SelectSC(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type BOOLEAN
	*@note empty
	*/
	public static final int F_up_down = 0;
	/**
	*@type INT
	*@note empty
	*/
	public static final int F_idx = 1;

}