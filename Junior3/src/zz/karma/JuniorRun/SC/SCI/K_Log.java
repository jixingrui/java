package zz.karma.JuniorRun.SC.SCI;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Log extends KarmaReaderA {
	public static final int type = 36772029;

	public K_Log(KarmaSpace space) {
		super(space, type , 112281120);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		name = karma.getString(0);
		note = karma.getString(1);
	}

	@Override
	public Karma toKarma() {
		karma.setString(0, name);
		karma.setString(1, note);
		return karma;
	}

	/**
	*@type STRING
	*@note name
	*/
	public String name;
	/**
	*@type STRING
	*@note empty
	*/
	public String note;

}