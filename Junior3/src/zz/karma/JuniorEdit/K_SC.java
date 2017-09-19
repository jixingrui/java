package zz.karma.JuniorEdit;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SC extends KarmaReaderA {
	public static final int type = 18383265;

	public K_SC(KarmaSpace space) {
		super(space, type , 70158939);
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
	*<p>[SaveRet] empty
	*<p>[SdkRet] empty
	*<p>[ReportIdeaRet] empty
	*<p>[TestRunRet] empty
	*<p>[SelectIdeaRet] empty
	*@note empty
	*/
	public Karma send;

	public static final int T_TestRunRet = 65371169;
	public static final int T_SelectIdeaRet = 70158554;
	public static final int T_SdkRet = 18383838;
	public static final int T_ReportIdeaRet = 18383840;
	public static final int T_SaveRet = 18383814;
}