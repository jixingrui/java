package zz.karma.JuniorRun.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Play extends KarmaReaderA {
	public static final int type = 29863277;

	public K_Play(KarmaSpace space) {
		super(space, type , 29863760);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		idScene = karma.getInt(0);
		idxRole = karma.getInt(1);
		idEgo = karma.getInt(2);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, idScene);
		karma.setInt(1, idxRole);
		karma.setInt(2, idEgo);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int idScene;
	/**
	*@type INT
	*@note empty
	*/
	public int idxRole;
	/**
	*@type INT
	*@note empty
	*/
	public int idEgo;

}