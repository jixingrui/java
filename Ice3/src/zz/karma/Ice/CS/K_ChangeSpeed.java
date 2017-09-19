package zz.karma.Ice.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_ChangeSpeed extends KarmaReaderA {
	public static final int type = 93487160;

	public K_ChangeSpeed(KarmaSpace space) {
		super(space, type , 93525171);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		speed = karma.getInt(0);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, speed);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int speed;

}