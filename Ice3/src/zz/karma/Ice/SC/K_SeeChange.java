package zz.karma.Ice.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SeeChange extends KarmaReaderA {
	public static final int type = 94033940;

	public K_SeeChange(KarmaSpace space) {
		super(space, type , 110063164);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		id = karma.getInt(0);
		form = karma.getBytes(1);
		angle = karma.getInt(2);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, id);
		karma.setBytes(1, form);
		karma.setInt(2, angle);
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
	public byte[] form;
	/**
	*@type INT
	*@note empty
	*/
	public int angle;

}