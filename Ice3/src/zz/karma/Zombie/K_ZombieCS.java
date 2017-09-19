package zz.karma.Zombie;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_ZombieCS extends KarmaReaderA {
	public static final int type = 94417305;

	public K_ZombieCS(KarmaSpace space) {
		super(space, type , 118541581);
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
	*<p>[IceCS] empty
	*<p>[CreateNpc] empty
	*<p>[MazeInfo] empty
	*<p>[FindPathRet] empty
	*<p>[RandDestRet] empty
	*<p>[EscapeRet] empty
	*@note empty
	*/
	public Karma msg;

	public static final int T_MazeInfo = 98702674;
	public static final int T_EscapeRet = 118541137;
	public static final int T_IceCS = 94410550;
	public static final int T_FindPathRet = 99367636;
	public static final int T_CreateNpc = 94411149;
	public static final int T_RandDestRet = 110963347;
}