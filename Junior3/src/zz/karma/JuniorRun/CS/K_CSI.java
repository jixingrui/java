package zz.karma.JuniorRun.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_CSI extends KarmaReaderA {
	public static final int type = 18828980;

	public K_CSI(KarmaSpace space) {
		super(space, type , 31724299);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		msg = karma.getKarma(0);
		idEgo = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setKarma(0, msg);
		karma.setInt(1, idEgo);
		return karma;
	}

	/**
	*@type KARMA
	*<p>[TurnOn] empty
	*<p>[TurnOff] empty
	*<p>[AddValue] empty
	*<p>[SetValue] empty
	*<p>[AskState] empty
	*<p>[AskR] empty
	*@note empty
	*/
	public Karma msg;
	/**
	*@type INT
	*@note empty
	*/
	public int idEgo;

	public static final int T_AddValue = 18829343;
	public static final int T_TurnOff = 18829341;
	public static final int T_TurnOn = 18829339;
	public static final int T_AskR = 31722163;
	public static final int T_AskState = 18829347;
	public static final int T_SetValue = 18829345;
}