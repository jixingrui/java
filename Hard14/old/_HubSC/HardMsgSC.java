package zzz.karma._Hard._Hub._HubSC;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class HardMsgSC extends KarmaReaderA {
	public static final int type = 18406057;
	public static final int version = 18912967;

	public HardMsgSC(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type INT
	*@note empty
	*/
	public static final int F_idxHard = 0;
	/**
	*@type KARMA
	*<p>[SC] empty
	*@note empty
	*/
	public static final int F_msgSC = 1;

	public static final int T_SC = 18389853;
}