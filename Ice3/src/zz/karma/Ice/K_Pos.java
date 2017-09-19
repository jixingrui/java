package zz.karma.Ice;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Pos extends KarmaReaderA {
	public static final int type = 93485143;

	public K_Pos(KarmaSpace space) {
		super(space, type , 93560353);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		x = karma.getInt(0);
		y = karma.getInt(1);
		z = karma.getInt(2);
		angle = karma.getInt(3);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, x);
		karma.setInt(1, y);
		karma.setInt(2, z);
		karma.setInt(3, angle);
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
	public int z;
	/**
	*@type INT
	*@note empty
	*/
	public int angle;

}