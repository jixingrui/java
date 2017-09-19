package zz.karma.Ice4;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_CS extends KarmaReaderA {
	public static final int type = 113103796;

	public K_CS(KarmaSpace space) {
		super(space, type , 113107682);
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
	*<p>[Act] empty
	*<p>[NewRoom] empty
	*@note empty
	*/
	public Karma msg;

	public static final int T_Act = 113091737;
	public static final int T_NewRoom = 113101339;
}