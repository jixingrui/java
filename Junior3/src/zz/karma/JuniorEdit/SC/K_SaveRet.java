package zz.karma.JuniorEdit.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SaveRet extends KarmaReaderA {
	public static final int type = 18383814;

	public K_SaveRet(KarmaSpace space) {
		super(space, type , 18913498);
	}

	@Override
	public void fromKarma(Karma karma) {
		version = karma.getInt(0);
		db = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, version);
		karma.setBytes(1, db);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int version;
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] db;

}