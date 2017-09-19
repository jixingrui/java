package zz.karma.Hard.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SelectCS extends KarmaReaderA {
	public static final int type = 18416116;

	public K_SelectCS(KarmaSpace space) {
		super(space, type , 18912947);
	}

	@Override
	public void fromKarma(Karma karma) {
		up_down = karma.getBoolean(0);
		idx = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setBoolean(0, up_down);
		karma.setInt(1, idx);
		return karma;
	}

	/**
	*@type BOOLEAN
	*@note empty
	*/
	public boolean up_down;
	/**
	*@type INT
	*@note empty
	*/
	public int idx;

}