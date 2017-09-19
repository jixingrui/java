package zz.karma.Ice.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.Ice.K_Pos;

/**
*@note empty
*/
public class K_Jump extends KarmaReaderA {
	public static final int type = 93485716;

	public K_Jump(KarmaSpace space) {
		super(space, type , 93525169);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		to.fromKarma(karma.getKarma(0));
	}

	@Override
	public Karma toKarma() {
		if(to != null)
			karma.setKarma(0, to.toKarma());
		return karma;
	}

	/**
	*@type KARMA
	*<p>[Pos] empty
	*@note empty
	*/
	public K_Pos to=new K_Pos(space);

	public static final int T_Pos = 93485143;
}