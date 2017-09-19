package zz.karma.Hard.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_AskMore extends KarmaReaderA {
	public static final int type = 18419577;

	public K_AskMore(KarmaSpace space) {
		super(space, type , 18912963);
	}

	@Override
	public void fromKarma(Karma karma) {
		pageSize = karma.getInt(0);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, pageSize);
		return karma;
	}

	/**
	*@type INT
	*@note full page size
	*/
	public int pageSize;

}