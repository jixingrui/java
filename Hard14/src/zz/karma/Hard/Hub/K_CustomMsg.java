package zz.karma.Hard.Hub;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_CustomMsg extends KarmaReaderA {
	public static final int type = 18389991;

	public K_CustomMsg(KarmaSpace space) {
		super(space, type , 18912907);
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