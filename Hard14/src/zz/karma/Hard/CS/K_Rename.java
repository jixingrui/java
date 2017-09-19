package zz.karma.Hard.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note depends on selected
*/
public class K_Rename extends KarmaReaderA {
	public static final int type = 18416120;

	public K_Rename(KarmaSpace space) {
		super(space, type , 18912951);
	}

	@Override
	public void fromKarma(Karma karma) {
		name = karma.getString(0);
	}

	@Override
	public Karma toKarma() {
		karma.setString(0, name);
		return karma;
	}

	/**
	*@type STRING
	*@note new name
	*/
	public String name;

}