package zz.karma.JuniorEdit;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_CS extends KarmaReaderA {
	public static final int type = 18383336;

	public K_CS(KarmaSpace space) {
		super(space, type , 70160614);
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
	*<p>[Save] empty
	*<p>[Wipe] empty
	*<p>[Load] empty
	*<p>[Sdk] empty
	*<p>[ReportRelation] empty
	*<p>[TestRun] empty
	*<p>[SelectIdea] empty
	*<p>[SaveIdea] empty
	*@note empty
	*/
	public Karma send;

	public static final int T_Wipe = 18384280;
	public static final int T_TestRun = 65371461;
	public static final int T_SaveIdea = 70159980;
	public static final int T_Load = 18384283;
	public static final int T_Sdk = 18384286;
	public static final int T_ReportRelation = 18384290;
	public static final int T_Save = 18384277;
	public static final int T_SelectIdea = 70158352;
}