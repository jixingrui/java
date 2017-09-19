package zz.karma.Hard.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note depends on held
*<p>if C.State.selected_up_down=up drop钮是灰色
*<p>其他情况，发送
*/
public class K_Drop extends KarmaReaderA {
	public static final int type = 18416128;

	public K_Drop(KarmaSpace space) {
		super(space, type , 18912959);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}