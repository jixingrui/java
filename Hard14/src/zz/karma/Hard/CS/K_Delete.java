package zz.karma.Hard.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note depends on selected
*<p>if C.State.selected_up_down=up 删除钮是灰色
*<p>反之，发送
*/
public class K_Delete extends KarmaReaderA {
	public static final int type = 18416124;

	public K_Delete(KarmaSpace space) {
		super(space, type , 18912955);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}