package zz.karma.Ice4.CS.Act;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_ChangeSkin extends KarmaReaderA {
	public static final int type = 113097324;

	public K_ChangeSkin(KarmaSpace space) {
		super(space, type , 113099519);
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