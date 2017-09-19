package zz.karma.Maze;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.Maze.SC.K_SaveRet;

/**
*@note empty
*/
public class K_SC extends KarmaReaderA {
	public static final int type = 56645837;

	public K_SC(KarmaSpace space) {
		super(space, type , 58135105);
	}

	@Override
	public void fromKarma(Karma karma) {
		msg.fromKarma(karma.getKarma(0));
	}

	@Override
	public Karma toKarma() {
		if(msg != null)
			karma.setKarma(0, msg.toKarma());
		return karma;
	}

	/**
	*@type KARMA
	*<p>[SaveRet] empty
	*@note empty
	*/
	public K_SaveRet msg=new K_SaveRet(space);

	public static final int T_SaveRet = 56645893;
}