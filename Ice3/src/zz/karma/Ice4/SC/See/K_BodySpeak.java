package zz.karma.Ice4.SC.See;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_BodySpeak extends KarmaReaderA {
	public static final int type = 113087704;

	public K_BodySpeak(KarmaSpace space) {
		super(space, type , 113099513);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		id = karma.getInt(0);
		message = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, id);
		karma.setBytes(1, message);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int id;
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] message;

}