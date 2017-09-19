package zz.karma.JuniorRun.CS.CSI;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_AddValue extends KarmaReaderA {
	public static final int type = 18829343;

	public K_AddValue(KarmaSpace space) {
		super(space, type , 18913709);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		concept = karma.getInt(0);
		value = karma.getDouble(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, concept);
		karma.setDouble(1, value);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int concept;
	/**
	*@type DOUBLE
	*@note empty
	*/
	public double value;

}