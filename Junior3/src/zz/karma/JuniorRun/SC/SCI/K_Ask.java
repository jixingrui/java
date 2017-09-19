package zz.karma.JuniorRun.SC.SCI;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Ask extends KarmaReaderA {
	public static final int type = 31722927;

	public K_Ask(KarmaSpace space) {
		super(space, type , 39419625);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		concept = karma.getInt(0);
		idCycle = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, concept);
		karma.setInt(1, idCycle);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int concept;
	/**
	*@type INT
	*@note empty
	*/
	public int idCycle;

}