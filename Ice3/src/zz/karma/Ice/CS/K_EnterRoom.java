package zz.karma.Ice.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.Ice.K_Pos;

/**
*@note empty
*/
public class K_EnterRoom extends KarmaReaderA {
	public static final int type = 93484473;

	public K_EnterRoom(KarmaSpace space) {
		super(space, type , 121761294);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		roomUID = karma.getString(0);
		initialPos.fromKarma(karma.getKarma(1));
		form = karma.getBytes(2);
		base = karma.getBytes(3);
	}

	@Override
	public Karma toKarma() {
		karma.setString(0, roomUID);
		if(initialPos != null)
			karma.setKarma(1, initialPos.toKarma());
		karma.setBytes(2, form);
		karma.setBytes(3, base);
		return karma;
	}

	/**
	*@type STRING
	*@note empty
	*/
	public String roomUID;
	/**
	*@type KARMA
	*<p>[Pos] empty
	*@note empty
	*/
	public K_Pos initialPos=new K_Pos(space);
	/**
	*@type BYTES
	*@note 形象数据。包含图形和当前的动作。
	*/
	public byte[] form;
	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] base;

	public static final int T_Pos = 93485143;
}