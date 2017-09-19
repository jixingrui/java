package zz.karma.Ice.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 
*/
public class K_SendPublic extends KarmaReaderA {
	public static final int type = 94026582;

	public K_SendPublic(KarmaSpace space) {
		super(space, type , 94041402);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		msg = karma.getBytes(0);
	}

	@Override
	public Karma toKarma() {
		karma.setBytes(0, msg);
		return karma;
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] msg;

}