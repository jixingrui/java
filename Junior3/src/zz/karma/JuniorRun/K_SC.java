package zz.karma.JuniorRun;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SC extends KarmaReaderA {
	public static final int type = 18829676;

	public K_SC(KarmaSpace space) {
		super(space, type , 28071757);
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
	*<p>[NewEgoR] empty
	*<p>[SCI] empty
	*<p>[NewSceneR] empty
	*@note empty
	*/
	public Karma send;

	public static final int T_Mark = 18829759;
	public static final int T_NewSceneR = 28032256;
	public static final int T_SCI = 18829763;
	public static final int T_NewEgoR = 18829761;
}