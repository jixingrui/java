package zz.karma.JuniorEdit.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SdkRet extends KarmaReaderA {
	public static final int type = 18383838;

	public K_SdkRet(KarmaSpace space) {
		super(space, type , 18913500);
	}

	@Override
	public void fromKarma(Karma karma) {
		version = karma.getInt(0);
		data = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, version);
		karma.setBytes(1, data);
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
	public byte[] data;

}