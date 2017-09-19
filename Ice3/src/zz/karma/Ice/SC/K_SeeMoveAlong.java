package zz.karma.Ice.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SeeMoveAlong extends KarmaReaderA {
	public static final int type = 93489797;

	public K_SeeMoveAlong(KarmaSpace space) {
		super(space, type , 97787343);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		id = karma.getInt(0);
		path = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, id);
		karma.setBytes(1, path);
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
	public byte[] path;

}