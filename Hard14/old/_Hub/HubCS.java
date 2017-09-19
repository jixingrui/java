package zzz.karma._Hard._Hub;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class HubCS extends KarmaReaderA {
	public static final int type = 18389993;
	public static final int version = 18912909;

	public HubCS(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type KARMA
	*<p>[CustomMsg] empty
	*<p>[HardMsgCS] empty
	*@note empty
	*/
	public static final int F_send = 0;

	public static final int T_CustomMsg = 18389991;
	public static final int T_HardMsgCS = 18406023;
}