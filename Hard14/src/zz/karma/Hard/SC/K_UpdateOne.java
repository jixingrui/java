package zz.karma.Hard.SC;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.Hard.K_Item;

/**
*@note empty
*/
public class K_UpdateOne extends KarmaReaderA {
	public static final int type = 18410309;

	public K_UpdateOne(KarmaSpace space) {
		super(space, type , 18912923);
	}

	@Override
	public void fromKarma(Karma karma) {
		up_down = karma.getBoolean(0);
		idx = karma.getInt(1);
		item.fromKarma(karma.getKarma(2));
	}

	@Override
	public Karma toKarma() {
		karma.setBoolean(0, up_down);
		karma.setInt(1, idx);
		if(item != null)
			karma.setKarma(2, item.toKarma());
		return karma;
	}

	/**
	*@type BOOLEAN
	*@note empty
	*/
	public boolean up_down;
	/**
	*@type INT
	*@note empty
	*/
	public int idx;
	/**
	*@type KARMA
	*<p>[Item] empty
	*@note empty
	*/
	public K_Item item=new K_Item(space);

	public static final int T_Item = 18389685;
}