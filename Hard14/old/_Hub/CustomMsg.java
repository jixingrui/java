package zzz.karma._Hard._Hub;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class CustomMsg extends KarmaReaderA {
	public static final int type = 18389991;
	public static final int version = 18912907;

	public CustomMsg(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public static final int F_data = 0;

}