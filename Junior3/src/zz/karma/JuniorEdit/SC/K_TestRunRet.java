package zz.karma.JuniorEdit.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_TestRunRet extends KarmaReaderA {
	public static final int type = 65371169;

	public K_TestRunRet(KarmaSpace space) {
		super(space, type , 65371615);
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