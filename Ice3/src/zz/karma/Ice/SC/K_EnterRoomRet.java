package zz.karma.Ice.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_EnterRoomRet extends KarmaReaderA {
	public static final int type = 93489339;

	public K_EnterRoomRet(KarmaSpace space) {
		super(space, type , 93525174);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		id = karma.getInt(0);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, id);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int id;

}