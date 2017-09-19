package zz.karma;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Maze extends KarmaReaderA {
	public static final int type = 56529293;

	public K_Maze(KarmaSpace space) {
		super(space, type , 56532152);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}