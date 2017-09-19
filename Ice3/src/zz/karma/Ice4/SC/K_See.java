package zz.karma.Ice4.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_See extends KarmaReaderA {
	public static final int type = 113087473;

	public K_See(KarmaSpace space) {
		super(space, type , 113107686);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		event = karma.getKarma(0);
		idRoom = karma.getInt(1);
	}

	@Override
	public Karma toKarma() {
		karma.setKarma(0, event);
		karma.setInt(1, idRoom);
		return karma;
	}

	/**
	*@type KARMA
	*<p>[BodyNew] empty
	*<p>[BodyMove] empty
	*<p>[BodyMoveAlong] empty
	*<p>[BodyChange] empty
	*<p>[BodySpeak] empty
	*<p>[BodyLeave] empty
	*<p>[NewBodyRet] empty
	*<p>[NewEyeRet] empty
	*@note empty
	*/
	public Karma event;
	/**
	*@type INT
	*@note empty
	*/
	public int idRoom;

	public static final int T_NewBodyRet = 113106831;
	public static final int T_BodySpeak = 113087704;
	public static final int T_BodyMove = 113087691;
	public static final int T_BodyLeave = 113087707;
	public static final int T_BodyMoveAlong = 113087700;
	public static final int T_BodyChange = 113087702;
	public static final int T_NewEyeRet = 113106835;
	public static final int T_BodyNew = 113087650;
}