package zzz.karma._Hard._Hub;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class HubSC extends KarmaReaderA {
	public static final int type = 18401777;
	public static final int version = 18912911;

	public HubSC(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type KARMA
	*<p>[CustomMsg] empty
	*<p>[HardMsgSC] empty
	*@note empty
	*/
	public static final int F_send = 0;

	public static final int T_HardMsgSC = 18406057;
	public static final int T_CustomMsg = 18389991;
}