package zz.karma.Ice;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Watch extends KarmaReaderA {
	public static final int type = 93513828;

	public K_Watch(KarmaSpace space) {
		super(space, type , 93525167);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		idTarget = karma.getInt(0);
		comparator = karma.getInt(1);
		limit = karma.getInt(2);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, idTarget);
		karma.setInt(1, comparator);
		karma.setInt(2, limit);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int idTarget;
	/**
	*@type INT
	*@note 0 : ==
*<p>1 : >
*<p>2 : <
*<p>3 : >=
*<p>4 : <=
	*/
	public int comparator;
	/**
	*@type INT
	*@note empty
	*/
	public int limit;

}