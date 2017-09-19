package zz.karma.JuniorEdit;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Concept extends KarmaReaderA {
	public static final int type = 18383338;

	public K_Concept(KarmaSpace space) {
		super(space, type , 72010314);
	}

	@Override
	public void fromKarma(Karma karma) {
		counterTrigger = karma.getInt(0);
		note = karma.getString(1);
		ioType = karma.getInt(2);
		id = karma.getInt(3);
		name = karma.getString(4);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, counterTrigger);
		karma.setString(1, note);
		karma.setInt(2, ioType);
		karma.setInt(3, id);
		karma.setString(4, name);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int counterTrigger;
	/**
	*@type STRING
	*@note empty
	*/
	public String note;
	/**
	*@type INT
	*@note empty
	*/
	public int ioType;
	/**
	*@type INT
	*@note empty
	*/
	public int id;
	/**
	*@type STRING
	*@note empty
	*/
	public String name;

}