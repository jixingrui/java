package zz.karma.Ice4.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_NewRoom extends KarmaReaderA {
	public static final int type = 113101339;

	public K_NewRoom(KarmaSpace space) {
		super(space, type , 113107685);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		token = karma.getInt(0);
		chamberSize = karma.getInt(1);
		z = karma.getInt(2);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, token);
		karma.setInt(1, chamberSize);
		karma.setInt(2, z);
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
	public int chamberSize;
	/**
	*@type INT
	*@note empty
	*/
	public int z;

}