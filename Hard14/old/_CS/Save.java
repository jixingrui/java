package zzz.karma._Hard._CS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note depends on selected
*/
public abstract class Save extends KarmaReaderA {
	public static final int type = 18416122;
	public static final int version = 18912953;

	public Save(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public static final int F_data = 0;

}