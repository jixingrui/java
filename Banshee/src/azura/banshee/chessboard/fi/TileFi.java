package azura.banshee.chessboard.fi;

import org.apache.log4j.Logger;

import common.algorithm.FoldIndex;

public class TileFi<T extends TileFi<T>> extends FoldIndex {
	public static Logger log = Logger.getLogger(TileFi.class);

	protected PyramidFi<T> pyramid;

	public TileFi(int fi, PyramidFi<T> pyramid) {
		super(fi);
		this.pyramid = pyramid;
	}

}
