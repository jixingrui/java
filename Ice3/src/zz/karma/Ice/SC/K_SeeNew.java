package zz.karma.Ice.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.Ice.K_Pos;

/**
*@note empty
*/
public class K_SeeNew extends KarmaReaderA {
	public static final int type = 93487617;

	public K_SeeNew(KarmaSpace space) {
		super(space, type , 93525175);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		pos.fromKarma(karma.getKarma(0));
		form = karma.getBytes(1);
		id = karma.getInt(2);
	}

	@Override
	public Karma toKarma() {
		if(pos != null)
			karma.setKarma(0, pos.toKarma());
		karma.setBytes(1, form);
		karma.setInt(2, id);
		return karma;
	}

	/**
	*@type KARMA
	*<p>[Pos] empty
	*@note empty
	*/
	public K_Pos pos=new K_Pos(space);
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] form;
	/**
	*@type INT
	*@note empty
	*/
	public int id;

	public static final int T_Pos = 93485143;
}