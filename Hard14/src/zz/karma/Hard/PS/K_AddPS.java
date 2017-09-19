package zz.karma.Hard.PS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 根据State.selectedIdx是不是-1和State.selected_up_down的值，分成两种情况：没选中和下选中
*<p>没选中时，生成一个Item，放在数据库队尾
*<p>下选中时，生成一个Item，放在数据库selectedItem的后面
*<p>S.State.upList最后一个Item.numChildren加1
*<p>之后生成RefillPS触发它
*/
public class K_AddPS extends KarmaReaderA {
	public static final int type = 18390037;

	public K_AddPS(KarmaSpace space) {
		super(space, type , 18912929);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}