package zzz.karma._Hard._PS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 硬性规定每次追加一页，
*
*delta：从数据库中拿的一段数据，它是从S.State.downList最后一个Item后面的Item开始，拿S.State.pageSize个。（downList取决于母节点）
*
*
*把delta追加给S.State.downList，生成AppendDown，
*delta->AppendDown.itemList
*{如果delta的数量等于S.State.pageSize
*flase->AppendDown.end
*如果delta的数量小于S.State.pageSize
*true->AppendDown.end}
*AppendDown->SC.send,发送出去
*
*（如果尾巴恰好在pageSize的整数倍，刚好传过去的时候，是看不到end标记的，要下一次再拖拽一下，会把end标记传过去）
*/
public abstract class AppendDownPS extends KarmaReaderA {
	public static final int type = 18413734;
	public static final int version = 18912931;

	public AppendDownPS(KarmaSpace space) {
		super(space, type , version);
	}


}