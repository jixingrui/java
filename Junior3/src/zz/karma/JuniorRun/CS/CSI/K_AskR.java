package zz.karma.JuniorRun.CS.CSI;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_AskR extends KarmaReaderA {
	public static final int type = 31722163;

	public K_AskR(KarmaSpace space) {
		super(space, type , 39419624);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		concept = karma.getInt(0);
		value = karma.getBoolean(1);
		idCycle = karma.getInt(2);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, concept);
		karma.setBoolean(1, value);
		karma.setInt(2, idCycle);
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
	public boolean value;
	/**
	*@type INT
	*@note empty
	*/
	public int idCycle;

}