package zz.karma.Maze.CS;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note empty
*/
public class K_Load extends KarmaReaderA {
	public static final int type = 56645353;

	public K_Load(KarmaSpace space) {
		super(space, type , 56646622);
	}

	@Override
	public void fromKarma(Karma karma) {
		data = karma.getBytes(0);
	}

	@Override
	public Karma toKarma() {
		karma.setBytes(0, data);
		return karma;
	}

	/**
	*@type BYTES
	*@note empty
	*/
	public byte[] data;

}