package zz.karma.JuniorRun.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Mark extends KarmaReaderA {
	public static final int type = 18828973;

	public K_Mark(KarmaSpace space) {
		super(space, type , 18913691);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		number = karma.getInt(0);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, number);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int number;

}