package zz.karma.JuniorEdit.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.JuniorEdit.K_Idea;

/**
*@note empty
*/
public class K_SaveIdea extends KarmaReaderA {
	public static final int type = 70159980;

	public K_SaveIdea(KarmaSpace space) {
		super(space, type , 70160616);
	}

	@Override
	public void fromKarma(Karma karma) {
		idea.fromKarma(karma.getKarma(0));
	}

	@Override
	public Karma toKarma() {
		if(idea != null)
			karma.setKarma(0, idea.toKarma());
		return karma;
	}

	/**
	*@type KARMA
	*<p>[Idea] empty
	*@note empty
	*/
	public K_Idea idea=new K_Idea(space);

	public static final int T_Idea = 69803052;
}