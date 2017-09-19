package zz.karma.Zombie.ZombieCS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_IceCS extends KarmaReaderA {
	public static final int type = 94410550;

	public K_IceCS(KarmaSpace space) {
		super(space, type , 94418142);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		msg = karma.getBytes(0);
	}

	@Override
	public Karma toKarma() {
		karma.setBytes(0, msg);
		return karma;
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] msg;

}