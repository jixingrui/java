package zz.karma.JuniorRun.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SCI extends KarmaReaderA {
	public static final int type = 18829763;

	public K_SCI(KarmaSpace space) {
		super(space, type , 36773113);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		msg = karma.getKarma(0);
		idEgo = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setKarma(0, msg);
		karma.setInt(1, idEgo);
		return karma;
	}

	/**
	*@type KARMA
	*<p>[Output] empty
	*<p>[Ask] empty
	*<p>[AskStateR] empty
	*<p>[Log] empty
	*@note empty
	*/
	public Karma msg;
	/**
	*@type INT
	*@note empty
	*/
	public int idEgo;

	public static final int T_AskStateR = 18829950;
	public static final int T_Output = 18829948;
	public static final int T_Ask = 31722927;
	public static final int T_Log = 36772029;
}