package zz.karma.Hard;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note send msg from c to s
*/
public class K_CS extends KarmaReaderA {
	public static final int type = 18389857;

	public K_CS(KarmaSpace space) {
		super(space, type , 18912905);
	}

	@Override
	public void fromKarma(Karma karma) {
		send = karma.getKarma(0);
	}

	@Override
	public Karma toKarma() {
		karma.setKarma(0, send);
		return karma;
	}

	/**
	*@type KARMA
	*<p>[Add] empty
	*<p>[SelectCS] empty
	*<p>[UnselectCS] empty
	*<p>[Rename] empty
	*<p>[Save] empty
	*<p>[Delete] empty
	*<p>[Hold] empty
	*<p>[Drop] empty
	*<p>[Jump] empty
	*<p>[AskMore] empty
	*@note pack me and send to server，数据同步到界面上去
	*/
	public Karma send;

	public static final int T_AskMore = 18419577;
	public static final int T_Rename = 18416120;
	public static final int T_Save = 18416122;
	public static final int T_Delete = 18416124;
	public static final int T_Hold = 18416126;
	public static final int T_Drop = 18416128;
	public static final int T_Jump = 18416130;
	public static final int T_SelectCS = 18416116;
	public static final int T_Add = 18390071;
	public static final int T_UnselectCS = 18416118;
}