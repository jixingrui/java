package zzz.karma._Hard._PS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 把S.State.downList清空
*ClearDown->SC.send,发送出去
*/
public abstract class ClearDownPS extends KarmaReaderA {
	public static final int type = 18413736;
	public static final int version = 18912933;

	public ClearDownPS(KarmaSpace space) {
		super(space, type , version);
	}


}