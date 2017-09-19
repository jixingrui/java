package zz.karma.JuniorEdit.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_ReportIdeaRet extends KarmaReaderA {
	public static final int type = 18383840;

	public K_ReportIdeaRet(KarmaSpace space) {
		super(space, type , 18913502);
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