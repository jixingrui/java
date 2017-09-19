package zz.karma.Zombie.ZombieSC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_RandDest extends KarmaReaderA {
	public static final int type = 110962795;

	public K_RandDest(KarmaSpace space) {
		super(space, type , 110963651);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		session = karma.getInt(0);
		xFrom = karma.getInt(1);
		yTo = karma.getInt(2);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, session);
		karma.setInt(1, xFrom);
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
	public int xFrom;
	/**
	*@type INT
	*@note empty
	*/
	public int yTo;

}