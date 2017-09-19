package zz.karma.Hard.Hub.HubCS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.Hard.K_CS;

/**
*@note empty
*/
public class K_HardMsgCS extends KarmaReaderA {
	public static final int type = 18406023;

	public K_HardMsgCS(KarmaSpace space) {
		super(space, type , 18912965);
	}

	@Override
	public void fromKarma(Karma karma) {
		idxHard = karma.getInt(0);
		msgCS.fromKarma(karma.getKarma(1));
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, idxHard);
		if(msgCS != null)
			karma.setKarma(1, msgCS.toKarma());
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int idxHard;
	/**
	*@type KARMA
	*<p>[CS] empty
	*@note empty
	*/
	public K_CS msgCS=new K_CS(space);

	public static final int T_CS = 18389857;
}