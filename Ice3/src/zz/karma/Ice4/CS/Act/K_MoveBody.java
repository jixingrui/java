package zz.karma.Ice4.CS.Act;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_MoveBody extends KarmaReaderA {
	public static final int type = 113091809;

	public K_MoveBody(KarmaSpace space) {
		super(space, type , 113099517);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		id = karma.getInt(0);
		x = karma.getInt(1);
		y = karma.getInt(2);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, id);
		karma.setInt(1, x);
		karma.setInt(2, y);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int id;
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