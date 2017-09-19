package zz.karma.JuniorRun.SC.SCI;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Output extends KarmaReaderA {
	public static final int type = 18829948;

	public K_Output(KarmaSpace space) {
		super(space, type , 18913715);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		concept = karma.getInt(0);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, concept);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int concept;

}