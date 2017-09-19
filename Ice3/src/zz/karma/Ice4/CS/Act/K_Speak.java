package zz.karma.Ice4.CS.Act;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Speak extends KarmaReaderA {
	public static final int type = 113097540;

	public K_Speak(KarmaSpace space) {
		super(space, type , 113099520);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		id = karma.getInt(0);
		message = karma.getBytes(1);
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, id);
		karma.setBytes(1, message);
		return karma;
	}

	/**
	*@type INT
	*@note empty
	*/
	public int id;
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] message;

}