package zzz.karma._Hard;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public abstract class Item extends KarmaReaderA {
	public static final int type = 18389685;
	public static final int version = 18912895;

	public Item(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type STRING
	*@note empty
	*/
	public static final int F_name = 0;
	/**
	*@type STRING
	*@note empty
	*/
	public static final int F_nameTail = 1;
	/**
	*@type INT
	*@note >0显示加号
	*/
	public static final int F_numChildren = 2;
	/**
	*@type INT
	*@note empty
	*/
	public static final int F_color = 3;
	/**
	*@type BYTES
	*@note empty
	*/
	public static final int F_data = 4;

}