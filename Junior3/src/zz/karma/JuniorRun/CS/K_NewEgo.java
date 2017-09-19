package zz.karma.JuniorRun.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_NewEgo extends KarmaReaderA {
	public static final int type = 18828978;

	public K_NewEgo(KarmaSpace space) {
		super(space, type , 18913695);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		identity = karma.getInt(0);
		token = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, identity);
		karma.setInt(1, token);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int identity;
	/**
	*@type INT
	*@note empty
	*/
	public int token;

}