package zz.karma.Ice4.CS.Act;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_NewBody extends KarmaReaderA {
	public static final int type = 113091802;

	public K_NewBody(KarmaSpace space) {
		super(space, type , 113099515);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		token = karma.getInt(0);
		x = karma.getInt(1);
		y = karma.getInt(2);
		angle = karma.getInt(3);
		skin = karma.getBytes(4);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, token);
		karma.setInt(1, x);
		karma.setInt(2, y);
		karma.setInt(3, angle);
		karma.setBytes(4, skin);
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
	public int angle;
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] skin;

}