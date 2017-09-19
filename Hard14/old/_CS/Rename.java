package zzz.karma._Hard._CS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note depends on selected
*/
public abstract class Rename extends KarmaReaderA {
	public static final int type = 18416120;
	public static final int version = 18912951;

	public Rename(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type STRING
	*@note new name
	*/
	public static final int F_name = 0;

}