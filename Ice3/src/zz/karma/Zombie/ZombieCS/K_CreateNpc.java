package zz.karma.Zombie.ZombieCS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.Zombie.K_Pos;

/**
*@note empty
*/
public class K_CreateNpc extends KarmaReaderA {
	public static final int type = 94411149;

	public K_CreateNpc(KarmaSpace space) {
		super(space, type , 94418143);
	}

	@Override
	public void fromKarma(Karma karma) {
		if(karma==null) return;
		aiType = karma.getInt(0);
		shape = karma.getString(1);
		mapBase = karma.getBytes(2);
		startPos.fromKarma(karma.getKarma(3));
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, aiType);
		karma.setString(1, shape);
		karma.setBytes(2, mapBase);
		if(startPos != null)
			karma.setKarma(3, startPos.toKarma());
		return karma;
	}

	/**
	*@type INT
	*@note 1=zombie
*<p>2=shooter
	*/
	public int aiType;
	/**
	*@type STRING
	*@note animal mc5
	*/
	public String shape;
	/**
	*@type BYTES
	*@note RoadMap.fromBytes();
	*/
	public byte[] mapBase;
	/**
	*@type KARMA
	*<p>[Pos] empty
	*@note empty
	*/
	public K_Pos startPos=new K_Pos(space);

	public static final int T_Pos = 94416506;
}