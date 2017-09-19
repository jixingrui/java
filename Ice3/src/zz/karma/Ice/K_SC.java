package zz.karma.Ice;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_SC extends KarmaReaderA {
	public static final int type = 93487551;

	public K_SC(KarmaSpace space) {
		super(space, type , 123987277);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		msg = karma.getKarma(0);
	}

	@Override
	public Karma toKarma() {
		karma.setKarma(0, msg);
		return karma;
	}

	/**
	*@type KARMA
	*<p>[EnterRoomRet] empty
	*<p>[SeeNew] empty
	*<p>[SeeMoveAlong] empty
	*<p>[SeeLeave] empty
	*<p>[WatchNotify] empty
	*<p>[ReceiveMsg] empty
	*<p>[SeeChange] empty
	*<p>[SeeMove] empty
	*<p>[SeeStop] empty
	*@note empty
	*/
	public Karma msg;

	public static final int T_ReceiveMsg = 94025674;
	public static final int T_SeeNew = 93487617;
	public static final int T_SeeLeave = 93524496;
	public static final int T_WatchNotify = 93523591;
	public static final int T_SeeStop = 123986839;
	public static final int T_SeeMoveAlong = 93489797;
	public static final int T_EnterRoomRet = 93489339;
	public static final int T_SeeChange = 94033940;
	public static final int T_SeeMove = 113916805;
}