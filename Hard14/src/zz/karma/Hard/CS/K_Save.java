package zz.karma.Hard.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note depends on selected
*/
public class K_Save extends KarmaReaderA {
	public static final int type = 18416122;

	public K_Save(KarmaSpace space) {
		super(space, type , 18912953);
	}

	@Override
	public void fromKarma(Karma karma) {
		data = karma.getBytes(0);
	}

	@Override
	public Karma toKarma() {
		karma.setBytes(0, data);
		return karma;
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] data;

}