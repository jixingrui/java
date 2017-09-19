package zz.karma.Ice4.SC.See;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_BodyLeave extends KarmaReaderA {
	public static final int type = 113087707;

	public K_BodyLeave(KarmaSpace space) {
		super(space, type , 113099514);
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