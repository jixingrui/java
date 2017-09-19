package zz.karma.Hard.Hub.HubSC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.Hard.K_SC;

/**
*@note empty
*/
public class K_HardMsgSC extends KarmaReaderA {
	public static final int type = 18406057;

	public K_HardMsgSC(KarmaSpace space) {
		super(space, type , 18912967);
	}

	@Override
	public void fromKarma(Karma karma) {
		idxHard = karma.getInt(0);
		msgSC.fromKarma(karma.getKarma(1));
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, idxHard);
		if(msgSC != null)
			karma.setKarma(1, msgSC.toKarma());
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int idxHard;
	/**
	*@type KARMA
	*<p>[SC] empty
	*@note empty
	*/
	public K_SC msgSC=new K_SC(space);

	public static final int T_SC = 18389853;
}