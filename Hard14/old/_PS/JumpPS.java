package zzz.karma._Hard._PS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 根据S.State.selected_up_down能分成两种情况：上跳，下跳
*
*上跳时：把S.State.selectedItem追加到S.State.upList的队尾
*
*下跳时：在S.State.upList里，把S.State.selectedItem和它之后的Item都移出
*
*生成RefillUp,把S.State.upList->RefillUp.itemList,发送RefillUp
*/
public abstract class JumpPS extends KarmaReaderA {
	public static final int type = 18413746;
	public static final int version = 18912943;

	public JumpPS(KarmaSpace space) {
		super(space, type , version);
	}


}