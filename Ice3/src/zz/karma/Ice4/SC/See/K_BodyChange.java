package zz.karma.Ice4.SC.See;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_BodyChange extends KarmaReaderA {
	public static final int type = 113087702;

	public K_BodyChange(KarmaSpace space) {
		super(space, type , 113099512);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		id = karma.getInt(0);
		skin = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, id);
		karma.setBytes(1, skin);
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
	public byte[] skin;

}