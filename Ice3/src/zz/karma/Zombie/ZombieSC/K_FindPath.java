package zz.karma.Zombie.ZombieSC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_FindPath extends KarmaReaderA {
	public static final int type = 99366954;

	public K_FindPath(KarmaSpace space) {
		super(space, type , 110963650);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		xStart = karma.getInt(0);
		yStart = karma.getInt(1);
		xEnd = karma.getInt(2);
		yEnd = karma.getInt(3);
		session = karma.getInt(4);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, xStart);
		karma.setInt(1, yStart);
		karma.setInt(2, xEnd);
		karma.setInt(3, yEnd);
		karma.setInt(4, session);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int xStart;
	/**
	*@type INT
	*@note empty
	*/
	public int yStart;
	/**
	*@type INT
	*@note empty
	*/
	public int xEnd;
	/**
	*@type INT
	*@note empty
	*/
	public int yEnd;
	/**
	*@type INT
	*@note empty
	*/
	public int session;

}