package zz.karma.Zombie.ZombieCS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_MazeInfo extends KarmaReaderA {
	public static final int type = 98702674;

	public K_MazeInfo(KarmaSpace space) {
		super(space, type , 98702897);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		data = karma.getBytes(0);
	}

	@Override
	public Karma toKarma() {
		karma.setBytes(0, data);
		return karma;
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] data;

}