package zz.karma.JuniorEdit.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_ReportRelation extends KarmaReaderA {
	public static final int type = 18384290;

	public K_ReportRelation(KarmaSpace space) {
		super(space, type , 18913512);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}