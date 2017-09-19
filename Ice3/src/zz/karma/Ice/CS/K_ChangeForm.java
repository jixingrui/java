package zz.karma.Ice.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note 
*/
public class K_ChangeForm extends KarmaReaderA {
	public static final int type = 93559043;

	public K_ChangeForm(KarmaSpace space) {
		super(space, type , 93560354);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		form = karma.getBytes(0);
	}

	@Override
	public Karma toKarma() {
		karma.setBytes(0, form);
		return karma;
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] form;

}