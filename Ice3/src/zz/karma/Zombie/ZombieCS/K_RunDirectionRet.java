package zz.karma.Zombie.ZombieCS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_RunDirectionRet extends KarmaReaderA {
	public static final int type = 118541137;

	public K_RunDirectionRet(KarmaSpace space) {
		super(space, type , 119393383);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		session = karma.getInt(0);
		angle = karma.getInt(1);
		x = karma.getInt(2);
		y = karma.getInt(3);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, session);
		karma.setInt(1, angle);
		karma.setInt(2, x);
		karma.setInt(3, y);
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
	public int x;
	/**
	*@type INT
	*@note empty
	*/
	public int y;

}