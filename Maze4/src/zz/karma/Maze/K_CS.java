package zz.karma.Maze;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_CS extends KarmaReaderA {
	public static final int type = 56645238;

	public K_CS(KarmaSpace space) {
		super(space, type , 68978266);
	}

	@Override
	public void fromKarma(Karma karma) {
		msg = karma.getKarma(0);
	}

	@Override
	public Karma toKarma() {
		karma.setKarma(0, msg);
		return karma;
	}

	/**
	*@type KARMA
	*<p>[Save] empty
	*<p>[Load] empty
	*<p>[Clear] empty
	*@note empty
	*/
	public Karma msg;

	public static final int T_Save = 56645347;
	public static final int T_Load = 56645353;
	public static final int T_Clear = 68977234;
}