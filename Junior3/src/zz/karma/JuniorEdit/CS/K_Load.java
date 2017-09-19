package zz.karma.JuniorEdit.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Load extends KarmaReaderA {
	public static final int type = 18384283;

	public K_Load(KarmaSpace space) {
		super(space, type , 18913508);
	}

	@Override
	public void fromKarma(Karma karma) {
		db = karma.getBytes(0);
	}

	@Override
	public Karma toKarma() {
		karma.setBytes(0, db);
		return karma;
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] db;

}