package zzz.karma._Hard._SC;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class UpdateOne extends KarmaReaderA {
	public static final int type = 18410309;
	public static final int version = 18912923;

	public UpdateOne(KarmaSpace space) {
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
	/**
	*@type KARMA
	*<p>[Item] empty
	*@note empty
	*/
	public static final int F_item = 2;

	public static final int T_Item = 18389685;
}