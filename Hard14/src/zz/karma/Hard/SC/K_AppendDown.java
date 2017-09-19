package zz.karma.Hard.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_AppendDown extends KarmaReaderA {
	public static final int type = 18390013;

	public K_AppendDown(KarmaSpace space) {
		super(space, type , 18912913);
	}

	@Override
	public void fromKarma(Karma karma) {
		end = karma.getBoolean(1);
	}

	@Override
	public Karma toKarma() {
		karma.setBoolean(1, end);
		return karma;
	}

	/**
	*@type LIST
	*<p>[Item] empty
	*@note empty
	*/
	public java.util.List<Karma> itemList=karma.getList(0);
	/**
	*@type BOOLEAN
	*@note empty
	*/
	public boolean end;

	public static final int T_Item = 18389685;
}