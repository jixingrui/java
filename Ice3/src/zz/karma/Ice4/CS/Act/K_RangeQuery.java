package zz.karma.Ice4.CS.Act;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_RangeQuery extends KarmaReaderA {
	public static final int type = 113097978;

	public K_RangeQuery(KarmaSpace space) {
		super(space, type , 113099521);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		x = karma.getInt(0);
		y = karma.getInt(1);
		width = karma.getInt(2);
		height = karma.getInt(3);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, x);
		karma.setInt(1, y);
		karma.setInt(2, width);
		karma.setInt(3, height);
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
	/**
	*@type INT
	*@note empty
	*/
	public int width;
	/**
	*@type INT
	*@note empty
	*/
	public int height;

}