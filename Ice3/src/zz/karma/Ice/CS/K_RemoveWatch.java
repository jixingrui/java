package zz.karma.Ice.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.Ice.K_Watch;

/**
*@note empty
*/
public class K_RemoveWatch extends KarmaReaderA {
	public static final int type = 93517606;

	public K_RemoveWatch(KarmaSpace space) {
		super(space, type , 93525172);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		watch.fromKarma(karma.getKarma(0));
	}

	@Override
	public Karma toKarma() {
		if(watch != null)
			karma.setKarma(0, watch.toKarma());
		return karma;
	}

	/**
	*@type KARMA
	*<p>[Watch] empty
	*@note empty
	*/
	public K_Watch watch=new K_Watch(space);

	public static final int T_Watch = 93513828;
}