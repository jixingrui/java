package zz.karma.Ice4.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Act extends KarmaReaderA {
	public static final int type = 113091737;

	public K_Act(KarmaSpace space) {
		super(space, type , 113107684);
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
	*<p>[NewBody] empty
	*<p>[NewEye] empty
	*<p>[MoveBody] empty
	*<p>[MoveEye] empty
	*<p>[ChangeSkin] empty
	*<p>[Speak] empty
	*<p>[RangeQuery] empty
	*@note empty
	*/
	public Karma event;
	/**
	*@type INT
	*@note empty
	*/
	public int idRoom;

	public static final int T_ChangeSkin = 113097324;
	public static final int T_NewEye = 113091807;
	public static final int T_NewBody = 113091802;
	public static final int T_RangeQuery = 113097978;
	public static final int T_Speak = 113097540;
	public static final int T_MoveBody = 113091809;
	public static final int T_MoveEye = 113091811;
}