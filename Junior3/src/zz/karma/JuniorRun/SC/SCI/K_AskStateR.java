package zz.karma.JuniorRun.SC.SCI;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_AskStateR extends KarmaReaderA {
	public static final int type = 18829950;

	public K_AskStateR(KarmaSpace space) {
		super(space, type , 18913717);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		concept = karma.getInt(0);
		state = karma.getBoolean(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, concept);
		karma.setBoolean(1, state);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int concept;
	/**
	*@type BOOLEAN
	*@note empty
	*/
	public boolean state;

}