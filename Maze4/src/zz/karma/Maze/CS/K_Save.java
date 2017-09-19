package zz.karma.Maze.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Save extends KarmaReaderA {
	public static final int type = 56645347;

	public K_Save(KarmaSpace space) {
		super(space, type , 56646621);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}