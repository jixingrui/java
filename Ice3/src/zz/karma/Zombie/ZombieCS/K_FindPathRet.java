package zz.karma.Zombie.ZombieCS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_FindPathRet extends KarmaReaderA {
	public static final int type = 99367636;

	public K_FindPathRet(KarmaSpace space) {
		super(space, type , 110963648);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		path = karma.getBytes(0);
		session = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setBytes(0, path);
		karma.setInt(1, session);
		return karma;
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] path;
	/**
	*@type INT
	*@note empty
	*/
	public int session;

}