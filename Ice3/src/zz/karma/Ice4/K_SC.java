package zz.karma.Ice4;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SC extends KarmaReaderA {
	public static final int type = 113103806;

	public K_SC(KarmaSpace space) {
		super(space, type , 113107683);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		msg = karma.getKarma(0);
	}

	@Override
	public Karma toKarma() {
		karma.setKarma(0, msg);
		return karma;
	}

	/**
	*@type KARMA
	*<p>[See] empty
	*<p>[NewRoomRet] empty
	*@note empty
	*/
	public Karma msg;

	public static final int T_NewRoomRet = 113102693;
	public static final int T_See = 113087473;
}