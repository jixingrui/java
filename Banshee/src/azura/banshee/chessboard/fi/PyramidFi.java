package azura.banshee.chessboard.fi;

import org.apache.log4j.Logger;

import com.esotericsoftware.kryo.util.IntMap;

import common.algorithm.FoldIndex;

public abstract class PyramidFi<T extends TileFi<T>> {
	public static Logger log = Logger.getLogger(PyramidFi.class);
	public final int tileSide = 512;

	public int zMax = -1;
	private IntMap<T> fi_TileFi = new IntMap<>();

	public PyramidFi() {
		FoldIndex.getAllFiInPyramid(zMax).forEach(fi -> {
			createTile(fi.fi);
		});
	}

	protected int globalToTile(int xy, int z) {
		if (z == 0) {
			return xy + tileSide / 2;
		} else {
			int result = xy % tileSide;
			return (result >= 0) ? result : result + tileSide;
		}
	}

	/**
	 * @param x
	 *            on z
	 * @param y
	 *            on z
	 * @param z
	 * @return tile or null
	 */
	public T getTile(int x, int y, int z) {
		if (z < 0 || z > zMax)
			return null;

		if (z == 0) {

			if (x >= -tileSide / 2 && x <= tileSide / 2 - 1 && y >= -tileSide / 2 && y <= tileSide / 2 - 1)
				return getTile(1);
			else
				return null;
		} else {
			int dx = FoldIndex.divide(x, tileSide);
			int dy = FoldIndex.divide(y, tileSide);
			FoldIndex fi = FoldIndex.create(dx, dy, z);
			if (fi != null)
				return getTile(fi.fi);
			else
				return null;
		}
	}

	public T getTile(int fi) {
		T result = fi_TileFi.get(fi);
		if (result == null) {
			result = createTile(fi);
			fi_TileFi.put(fi, result);
		}
		return result;
	}

	protected abstract T createTile(int fi);

}
