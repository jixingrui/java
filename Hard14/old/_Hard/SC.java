package zzz.karma._Hard;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class SC extends KarmaReaderA {
	public static final int type = 18389853;
	public static final int version = 18912901;

	public SC(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type KARMA
	*<p>[AppendDown] empty
	*<p>[ClearDown] empty
	*<p>[RefillUp] empty
	*<p>[UnselectSC] empty
	*<p>[SelectSC] empty
	*<p>[UpdateOne] empty
	*<p>[ClearHold] empty
	*<p>[ClearUp] empty
	*@note empty
	*/
	public static final int F_send = 0;

	public static final int T_AppendDown = 18390013;
	public static final int T_ClearDown = 18410301;
	public static final int T_ClearHold = 18410861;
	public static final int T_RefillUp = 18410303;
	public static final int T_UnselectSC = 18410305;
	public static final int T_SelectSC = 18410307;
	public static final int T_UpdateOne = 18410309;
	public static final int T_ClearUp = 18410311;
}