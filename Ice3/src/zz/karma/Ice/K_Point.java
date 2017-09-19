package zz.karma.Ice;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Point extends KarmaReaderA {
	public static final int type = 94023389;

	public K_Point(KarmaSpace space) {
		super(space, type , 94041398);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		x = karma.getInt(0);
		y = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, x);
		karma.setInt(1, y);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int x;
	/**
	*@type INT
	*@note empty
	*/
	public int y;

}