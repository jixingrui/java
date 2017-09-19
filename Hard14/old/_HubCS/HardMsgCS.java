package zzz.karma._Hard._Hub._HubCS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class HardMsgCS extends KarmaReaderA {
	public static final int type = 18406023;
	public static final int version = 18912965;

	public HardMsgCS(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type INT
	*@note empty
	*/
	public static final int F_idxHard = 0;
	/**
	*@type KARMA
	*<p>[CS] empty
	*@note empty
	*/
	public static final int F_msgCS = 1;

	public static final int T_CS = 18389857;
}