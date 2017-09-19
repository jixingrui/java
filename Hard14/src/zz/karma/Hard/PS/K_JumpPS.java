package zz.karma.Hard.PS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 根据S.State.selected_up_down能分成两种情况：上跳，下跳
*<p>上跳时：把S.State.selectedItem追加到S.State.upList的队尾
*<p>下跳时：在S.State.upList里，把S.State.selectedItem和它之后的Item都移出
*<p>生成RefillUp,把S.State.upList->RefillUp.itemList,发送RefillUp
*/
public class K_JumpPS extends KarmaReaderA {
	public static final int type = 18413746;

	public K_JumpPS(KarmaSpace space) {
		super(space, type , 18912943);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}