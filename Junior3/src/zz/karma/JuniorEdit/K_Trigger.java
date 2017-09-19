package zz.karma.JuniorEdit;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Trigger extends KarmaReaderA {
	public static final int type = 18383349;

	public K_Trigger(KarmaSpace space) {
		super(space, type , 18913496);
	}

	@Override
	public void fromKarma(Karma karma) {
		on = karma.getInt(0);
		off = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, on);
		karma.setInt(1, off);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int on;
	/**
	*@type INT
	*@note empty
	*/
	public int off;

}