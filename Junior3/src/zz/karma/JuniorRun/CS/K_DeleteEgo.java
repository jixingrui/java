package zz.karma.JuniorRun.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_DeleteEgo extends KarmaReaderA {
	public static final int type = 112038053;

	public K_DeleteEgo(KarmaSpace space) {
		super(space, type , 112038535);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		id = karma.getInt(0);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, id);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int id;

}