package zzz.karma._Hard;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class Hub extends KarmaReaderA {
	public static final int type = 18389692;
	public static final int version = 18912899;

	public Hub(KarmaSpace space) {
		super(space, type , version);
	}


}