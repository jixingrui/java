package zz.karma.Maze.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SaveRet extends KarmaReaderA {
	public static final int type = 56645893;

	public K_SaveRet(KarmaSpace space) {
		super(space, type , 58135106);
	}

	@Override
	public void fromKarma(Karma karma) {
		mazeData = karma.getBytes(0);
		slaveList = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setBytes(0, mazeData);
		karma.setBytes(1, slaveList);
		return karma;
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] mazeData;
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] slaveList;

}