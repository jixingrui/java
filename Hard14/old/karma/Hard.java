package zzz.karma;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note C:Client
*S:Server
*P:Programmer
*H:Hard general
*I:Hard Individual
*/
public abstract class Hard extends KarmaReaderA {
	public static final int type = 18389414;
	public static final int version = 18912893;

	public Hard(KarmaSpace space) {
		super(space, type , version);
	}


}