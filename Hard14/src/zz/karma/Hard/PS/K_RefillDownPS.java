package zz.karma.Hard.PS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 生成ClearDownPS,触发它，生成AppendDownPS,触发它
*/
public class K_RefillDownPS extends KarmaReaderA {
	public static final int type = 18413744;

	public K_RefillDownPS(KarmaSpace space) {
		super(space, type , 18912941);
	}

	@Override
	public void fromKarma(Karma karma) {
	}

	@Override
	public Karma toKarma() {
		return karma;
	}


}