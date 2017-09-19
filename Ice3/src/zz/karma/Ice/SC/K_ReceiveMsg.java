package zz.karma.Ice.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_ReceiveMsg extends KarmaReaderA {
	public static final int type = 94025674;

	public K_ReceiveMsg(KarmaSpace space) {
		super(space, type , 94041404);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		idFrom = karma.getInt(0);
		msg = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, idFrom);
		karma.setBytes(1, msg);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int idFrom;
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] msg;

}