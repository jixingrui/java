package zzz.karma._Hard._PS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 检查是否有重名：检查S.State.selectedItem与之并列的其他Item.name与Rename.name是否相同，相同视为重名
*
*如果不重名，生成UpdateOne，
*S.State.selectedItem->UpdateOne.item，
*S.State.selectedIdx->UpdateOne.idx
*S.State.selected_up_down->UpdateOne.up_down
*再把UpdateOne->SC.send，发送出去
*
*如果重名，结束
*/
public abstract class RenamePS extends KarmaReaderA {
	public static final int type = 18413738;
	public static final int version = 18912935;

	public RenamePS(KarmaSpace space) {
		super(space, type , version);
	}


}