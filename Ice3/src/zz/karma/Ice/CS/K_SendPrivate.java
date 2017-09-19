package zz.karma.Ice.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SendPrivate extends KarmaReaderA {
	public static final int type = 94026312;

	public K_SendPrivate(KarmaSpace space) {
		super(space, type , 94041401);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		idTo = karma.getInt(0);
		msg = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, idTo);
		karma.setBytes(1, msg);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int idTo;
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] msg;

}