package zz.karma.Maze;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Room extends KarmaReaderA {
	public static final int type = 56529372;

	public K_Room(KarmaSpace space) {
		super(space, type , 57934918);
	}

	@Override
	public void fromKarma(Karma karma) {
		tag = karma.getString(0);
		groundImage = karma.getBytes(1);
		mask = karma.getBytes(2);
		tid = karma.getInt(4);
		name = karma.getString(5);
		tidParent = karma.getInt(6);
	}

	@Override
	public Karma toKarma() {
		karma.setString(0, tag);
		karma.setBytes(1, groundImage);
		karma.setBytes(2, mask);
		karma.setInt(4, tid);
		karma.setString(5, name);
		karma.setInt(6, tidParent);
		return karma;
	}

	/**
	*@type STRING
	*@note empty
	*/
	public String tag;
	/**
	*@type BYTES
	*@note GalPack of .zebra
	*/
	public byte[] groundImage;
	/**
	*@type BYTES
	*@note GalPack of Zmask
	*/
	public byte[] mask;
	/**
	*@type LIST
	*<p>[Base] empty
	*@note empty
	*/
	public java.util.List<Karma> baseList=karma.getList(3);
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
	public int tidParent;

	public static final int T_Base = 56530018;
}