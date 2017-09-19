package zz.karma.Ice4.CS.Act;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_NewEye extends KarmaReaderA {
	public static final int type = 113091807;

	public K_NewEye(KarmaSpace space) {
		super(space, type , 113130134);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		token = karma.getInt(0);
		x = karma.getInt(1);
		y = karma.getInt(2);
		width = karma.getInt(3);
		height = karma.getInt(4);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, token);
		karma.setInt(1, x);
		karma.setInt(2, y);
		karma.setInt(3, width);
		karma.setInt(4, height);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int token;
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