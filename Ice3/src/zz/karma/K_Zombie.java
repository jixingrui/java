package zz.karma;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Zombie extends KarmaReaderA {
	public static final int type = 94410490;

	public K_Zombie(KarmaSpace space) {
		super(space, type , 94418138);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}