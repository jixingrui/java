package zz.karma.Ice;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_CS extends KarmaReaderA {
	public static final int type = 93484375;

	public K_CS(KarmaSpace space) {
		super(space, type , 109296932);
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
	*<p>[EnterRoom] empty
	*<p>[Jump] empty
	*<p>[Move] empty
	*<p>[ChangeSpeed] empty
	*<p>[AddWatch] empty
	*<p>[RemoveWatch] empty
	*<p>[ChangeForm] empty
	*<p>[SendPrivate] empty
	*<p>[SendPublic] empty
	*@note empty
	*/
	public Karma msg;

	public static final int T_ChangeForm = 93559043;
	public static final int T_Move = 93485888;
	public static final int T_SendPrivate = 94026312;
	public static final int T_AddWatch = 93522544;
	public static final int T_RemoveWatch = 93517606;
	public static final int T_Jump = 93485716;
	public static final int T_ChangeSpeed = 93487160;
	public static final int T_EnterRoom = 93484473;
	public static final int T_SendPublic = 94026582;
}