package zz.karma.JuniorRun.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_NewScene extends KarmaReaderA {
	public static final int type = 28030740;

	public K_NewScene(KarmaSpace space) {
		super(space, type , 28033312);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		script = karma.getInt(0);
		token = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, script);
		karma.setInt(1, token);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int script;
	/**
	*@type INT
	*@note empty
	*/
	public int token;

}