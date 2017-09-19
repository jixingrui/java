package zz.karma.JuniorRun;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_CS extends KarmaReaderA {
	public static final int type = 18828821;

	public K_CS(KarmaSpace space) {
		super(space, type , 112038534);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		send = karma.getKarma(0);
	}

	@Override
	public Karma toKarma() {
		karma.setKarma(0, send);
		return karma;
	}

	/**
	*@type KARMA
	*<p>[Mark] empty
	*<p>[SetOutputLevel] empty
	*<p>[NewEgo] empty
	*<p>[CSI] empty
	*<p>[NewScene] empty
	*<p>[Play] empty
	*<p>[DeleteEgo] empty
	*<p>[DeleteScene] empty
	*@note empty
	*/
	public Karma send;

	public static final int T_Mark = 18828973;
	public static final int T_DeleteScene = 112038058;
	public static final int T_DeleteEgo = 112038053;
	public static final int T_Play = 29863277;
	public static final int T_CSI = 18828980;
	public static final int T_NewEgo = 18828978;
	public static final int T_SetOutputLevel = 18828976;
	public static final int T_NewScene = 28030740;
}