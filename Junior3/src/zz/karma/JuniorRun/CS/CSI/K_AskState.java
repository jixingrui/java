package zz.karma.JuniorRun.CS.CSI;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_AskState extends KarmaReaderA {
	public static final int type = 18829347;

	public K_AskState(KarmaSpace space) {
		super(space, type , 18913713);
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