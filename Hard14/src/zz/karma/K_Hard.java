package zz.karma;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note C:Client
*<p>S:Server
*<p>P:Programmer
*<p>H:Hard general
*<p>I:Hard Individual
*/
public class K_Hard extends KarmaReaderA {
	public static final int type = 18389414;

	public K_Hard(KarmaSpace space) {
		super(space, type , 18912893);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}