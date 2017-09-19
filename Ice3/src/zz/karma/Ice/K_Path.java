package zz.karma.Ice;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Path extends KarmaReaderA {
	public static final int type = 94023610;

	public K_Path(KarmaSpace space) {
		super(space, type , 94041399);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
	}

	@Override
	public Karma toKarma() {
		return karma;
	}

	/**
	*@type LIST
	*<p>[Point] empty
	*@note empty
	*/
	public java.util.List<Karma> path=karma.getList(0);

	public static final int T_Point = 94023389;
}