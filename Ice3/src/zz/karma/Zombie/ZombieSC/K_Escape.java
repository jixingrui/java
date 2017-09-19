package zz.karma.Zombie.ZombieSC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Escape extends KarmaReaderA {
	public static final int type = 118540453;

	public K_Escape(KarmaSpace space) {
		super(space, type , 121126495);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		session = karma.getInt(0);
		xRunner = karma.getInt(1);
		yRunner = karma.getInt(2);
		dist = karma.getInt(3);
		xMonster = karma.getInt(4);
		yMonster = karma.getInt(5);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, session);
		karma.setInt(1, xRunner);
		karma.setInt(2, yRunner);
		karma.setInt(3, dist);
		karma.setInt(4, xMonster);
		karma.setInt(5, yMonster);
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
	public int xRunner;
	/**
	*@type INT
	*@note empty
	*/
	public int yRunner;
	/**
	*@type INT
	*@note empty
	*/
	public int dist;
	/**
	*@type INT
	*@note empty
	*/
	public int xMonster;
	/**
	*@type INT
	*@note empty
	*/
	public int yMonster;

}