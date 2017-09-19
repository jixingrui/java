package zz.karma.JuniorRun.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SetOutputLevel extends KarmaReaderA {
	public static final int type = 18828976;

	public K_SetOutputLevel(KarmaSpace space) {
		super(space, type , 18913693);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		level = karma.getInt(0);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, level);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int level;

}