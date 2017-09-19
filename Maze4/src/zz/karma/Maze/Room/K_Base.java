package zz.karma.Maze.Room;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Base extends KarmaReaderA {
	public static final int type = 56530018;

	public K_Base(KarmaSpace space) {
		super(space, type , 56619176);
	}

	@Override
	public void fromKarma(Karma karma) {
		name = karma.getString(0);
		zbase = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setString(0, name);
		karma.setBytes(1, zbase);
		return karma;
	}

	/**
	*@type STRING
	*@note empty
	*/
	public String name;
	/**
	*@type BYTES
	*@note data
	*/
	public byte[] zbase;

}