package zzz.karma._Hard._CS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note depends on held
*
*if C.State.selected_up_down=up drop钮是灰色
*其他情况，发送
*/
public abstract class Drop extends KarmaReaderA {
	public static final int type = 18416128;
	public static final int version = 18912959;

	public Drop(KarmaSpace space) {
		super(space, type , version);
	}


}