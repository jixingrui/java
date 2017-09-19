package zz.karma.Zombie;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_ZombieSC extends KarmaReaderA {
	public static final int type = 94417820;

	public K_ZombieSC(KarmaSpace space) {
		super(space, type , 118541582);
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
	*<p>[IceSC] empty
	*<p>[FindPath] empty
	*<p>[RandDest] empty
	*<p>[Escape] empty
	*@note empty
	*/
	public Karma msg;

	public static final int T_Escape = 118540453;
	public static final int T_FindPath = 99366954;
	public static final int T_RandDest = 110962795;
	public static final int T_IceSC = 94410968;
}