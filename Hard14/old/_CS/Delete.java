package zzz.karma._Hard._CS;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note depends on selected
*
*if C.State.selected_up_down=up 删除钮是灰色
*反之，发送
*/
public abstract class Delete extends KarmaReaderA {
	public static final int type = 18416124;
	public static final int version = 18912955;

	public Delete(KarmaSpace space) {
		super(space, type , version);
	}


}