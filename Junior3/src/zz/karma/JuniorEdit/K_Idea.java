package zz.karma.JuniorEdit;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Idea extends KarmaReaderA {
	public static final int type = 69803052;

	public K_Idea(KarmaSpace space) {
		super(space, type , 92571204);
	}

	@Override
	public void fromKarma(Karma karma) {
		defaultValue = karma.getBoolean(0);
		note = karma.getString(1);
		flashy = karma.getBoolean(2);
		outLink = karma.getBoolean(3);
	}

	@Override
	public Karma toKarma() {
		karma.setBoolean(0, defaultValue);
		karma.setString(1, note);
		karma.setBoolean(2, flashy);
		karma.setBoolean(3, outLink);
		return karma;
	}

	/**
	*@type BOOLEAN
	*@note empty
	*/
	public boolean defaultValue;
	/**
	*@type STRING
	*@note empty
	*/
	public String note;
	/**
	*@type BOOLEAN
	*@note empty
	*/
	public boolean flashy;
	/**
	*@type BOOLEAN
	*@note empty
	*/
	public boolean outLink;

}