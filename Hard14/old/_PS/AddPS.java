package zzz.karma._Hard._PS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 根据State.selectedIdx是不是-1和State.selected_up_down的值，分成两种情况：没选中和下选中
*
*没选中时，生成一个Item，放在数据库队尾
*下选中时，生成一个Item，放在数据库selectedItem的后面
*
*S.State.upList最后一个Item.numChildren加1
*之后生成RefillPS触发它
*/
public abstract class AddPS extends KarmaReaderA {
	public static final int type = 18390037;
	public static final int version = 18912929;

	public AddPS(KarmaSpace space) {
		super(space, type , version);
	}


}