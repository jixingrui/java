package zz.karma.Maze.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Clear extends KarmaReaderA {
	public static final int type = 68977234;

	public K_Clear(KarmaSpace space) {
		super(space, type , 68978267);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}