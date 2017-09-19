package zz.karma.Zombie.ZombieCS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_EscapeRet extends KarmaReaderA {
	public static final int type = 118541137;

	public K_EscapeRet(KarmaSpace space) {
		super(space, type , 121126494);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		session = karma.getInt(0);
		path = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, session);
		karma.setBytes(1, path);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int session;
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] path;

}