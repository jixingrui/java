package zz.karma.Zombie.ZombieSC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_RunDirection extends KarmaReaderA {
	public static final int type = 118540453;

	public K_RunDirection(KarmaSpace space) {
		super(space, type , 119397546);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		session = karma.getInt(0);
		angle = karma.getInt(1);
		xFrom = karma.getInt(2);
		yFrom = karma.getInt(3);
		dist = karma.getInt(4);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, session);
		karma.setInt(1, angle);
		karma.setInt(2, xFrom);
		karma.setInt(3, yFrom);
		karma.setInt(4, dist);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int session;
	/**
	*@type INT
	*@note empty
	*/
	public int angle;
	/**
	*@type INT
	*@note empty
	*/
	public int xFrom;
	/**
	*@type INT
	*@note empty
	*/
	public int yFrom;
	/**
	*@type INT
	*@note empty
	*/
	public int dist;

}