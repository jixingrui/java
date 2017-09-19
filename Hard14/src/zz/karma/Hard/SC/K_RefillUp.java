package zz.karma.Hard.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_RefillUp extends KarmaReaderA {
	public static final int type = 18410303;

	public K_RefillUp(KarmaSpace space) {
		super(space, type , 18912917);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}

	/**
	*@type LIST
	*<p>[Item] empty
	*@note empty
	*/
	public java.util.List<Karma> itemList=karma.getList(0);

	public static final int T_Item = 18389685;
}