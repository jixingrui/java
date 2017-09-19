package zz.karma.Hard;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SC extends KarmaReaderA {
	public static final int type = 18389853;

	public K_SC(KarmaSpace space) {
		super(space, type , 18912901);
	}

	@Override
	public void fromKarma(Karma karma) {
		send = karma.getKarma(0);
	}

	@Override
	public Karma toKarma() {
		karma.setKarma(0, send);
		return karma;
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
	public Karma send;

	public static final int T_AppendDown = 18390013;
	public static final int T_ClearDown = 18410301;
	public static final int T_ClearHold = 18410861;
	public static final int T_RefillUp = 18410303;
	public static final int T_UnselectSC = 18410305;
	public static final int T_SelectSC = 18410307;
	public static final int T_UpdateOne = 18410309;
	public static final int T_ClearUp = 18410311;
}