package zz.karma.Zombie.ZombieCS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_RandDestRet extends KarmaReaderA {
	public static final int type = 110963347;

	public K_RandDestRet(KarmaSpace space) {
		super(space, type , 110963649);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		session = karma.getInt(0);
		xTo = karma.getInt(1);
		yTo = karma.getInt(2);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, session);
		karma.setInt(1, xTo);
		karma.setInt(2, yTo);
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
	public int xTo;
	/**
	*@type INT
	*@note empty
	*/
	public int yTo;

}