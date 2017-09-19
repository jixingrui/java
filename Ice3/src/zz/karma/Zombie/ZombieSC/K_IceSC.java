package zz.karma.Zombie.ZombieSC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_IceSC extends KarmaReaderA {
	public static final int type = 94410968;

	public K_IceSC(KarmaSpace space) {
		super(space, type , 94418144);
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