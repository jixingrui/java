package zz.karma.Hard;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Item extends KarmaReaderA {
	public static final int type = 18389685;

	public K_Item(KarmaSpace space) {
		super(space, type , 84998000);
	}

	@Override
	public void fromKarma(Karma karma) {
		name = karma.getString(0);
		nameTail = karma.getString(1);
		numChildren = karma.getInt(2);
		color = karma.getInt(3);
		data = karma.getBytes(4);
		sortValue = karma.getInt(5);
	}

	@Override
	public Karma toKarma() {
		karma.setString(0, name);
		karma.setString(1, nameTail);
		karma.setInt(2, numChildren);
		karma.setInt(3, color);
		karma.setBytes(4, data);
		karma.setInt(5, sortValue);
		return karma;
	}

	/**
	*@type STRING
	*@note empty
	*/
	public String name;
	/**
	*@type STRING
	*@note empty
	*/
	public String nameTail;
	/**
	*@type INT
	*@note >0显示加号
	*/
	public int numChildren;
	/**
	*@type INT
	*@note empty
	*/
	public int color;
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] data;
	/**
	*@type INT
	*@note empty
	*/
	public int sortValue;

}