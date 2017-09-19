package zz.karma.Ice4.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_NewRoomRet extends KarmaReaderA {
	public static final int type = 113102693;

	public K_NewRoomRet(KarmaSpace space) {
		super(space, type , 113107687);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		token = karma.getInt(0);
		id = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, token);
		karma.setInt(1, id);
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
	public int id;

}