package zz.karma.Ice.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Move extends KarmaReaderA {
	public static final int type = 93485888;

	public K_Move(KarmaSpace space) {
		super(space, type , 97785731);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		path = karma.getBytes(0);
	}

	@Override
	public Karma toKarma() {
		karma.setBytes(0, path);
		return karma;
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] path;

}