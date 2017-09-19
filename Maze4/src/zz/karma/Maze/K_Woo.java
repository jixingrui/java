package zz.karma.Maze;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Woo extends KarmaReaderA {
	public static final int type = 56529397;

	public K_Woo(KarmaSpace space) {
		super(space, type , 67388637);
	}

	@Override
	public void fromKarma(Karma karma) {
		tag = karma.getString(0);
		animal = karma.getBytes(1);
		doorToName = karma.getString(2);
		tid = karma.getInt(3);
		name = karma.getString(4);
		doorToTid = karma.getInt(5);
		x = karma.getInt(6);
		y = karma.getInt(7);
		angle = karma.getInt(8);
	}

	@Override
	public Karma toKarma() {
		karma.setString(0, tag);
		karma.setBytes(1, animal);
		karma.setString(2, doorToName);
		karma.setInt(3, tid);
		karma.setString(4, name);
		karma.setInt(5, doorToTid);
		karma.setInt(6, x);
		karma.setInt(7, y);
		karma.setInt(8, angle);
		return karma;
	}

	/**
	*@type STRING
	*@note empty
	*/
	public String tag;
	/**
	*@type BYTES
	*@note GalPack of button.animal
	*/
	public byte[] animal;
	/**
	*@type STRING
	*@note empty
	*/
	public String doorToName;
	/**
	*@type INT
	*@note empty
	*/
	public int tid;
	/**
	*@type STRING
	*@note empty
	*/
	public String name;
	/**
	*@type INT
	*@note empty
	*/
	public int doorToTid;
	/**
	*@type INT
	*@note empty
	*/
	public int x;
	/**
	*@type INT
	*@note empty
	*/
	public int y;
	/**
	*@type INT
	*@note empty
	*/
	public int angle;

}