package common.algorithm;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import common.collections.RectB;

/**
 * @param xyMapping
 *            from [-16384~16383] to [0,32767] at bottom level
 * @param xyLevels
 *            {0} {0,1} {0,1,2,3} ... {0...32767}
 * @param z
 *            [0,1,2 ... 15]
 * @return fi [1,2147483647]
 */
public class FoldIndex {
	private static final int zMax = 15;
	public final int fi, x, y, z, xp, yp;

	public FoldIndex(int fi) {
		if (fi <= 0)
			throw new Error();

		this.fi = fi;
		z = FastMath.log2(fi) >>> 1;
		int mask = FastMath.pow2x(z) - 1;
		xp = (fi >>> z) & mask;
		yp = fi & mask;

		int side = sideLength();
		x = xp - side / 2;
		y = yp - side / 2;
	}

	private FoldIndex(int fi, int x, int y, int z, int xp, int yp) {
		this.fi = fi;
		this.x = x;
		this.y = y;
		this.z = z;
		this.xp = xp;
		this.yp = yp;
	}

	public static FoldIndex create(int x, int y, int z) {
		int side = sideLength(z);
		int xp = x + side / 2;
		int yp = y + side / 2;

		int fi = 1 << (z * 2);
		fi |= xp << z;
		fi |= yp;

		if (z < 0 || z > zMax || xp < 0 || xp > side || yp < 0 || yp > side)
			return null;
		else
			return new FoldIndex(fi, x, y, z, xp, yp);
	}

	public static int divide(int num, int side) {
		if (num >= 0)
			return num / side;
		else
			return (num + 1) / side - 1;
	}

	public int sideLength() {
		return FastMath.pow2x(z);
	}

	public static int sideLength(int z) {
		return FastMath.pow2x(z);
	}

	public FoldIndex getUp() {
		if (z == 0)
			return null;
		else
			return create(xp >>> 1, yp >>> 1, z - 1);
	}

	public List<FoldIndex> getLow4() {
		if (z <= 0 || z >= 15)
			return null;

		List<FoldIndex> result = new ArrayList<>(4);
		result.add(create(xp << 1, yp << 1, z + 1));
		result.add(create(xp << 1 | 1, yp << 1, z + 1));
		result.add(create(xp << 1, yp << 1 | 1, z + 1));
		result.add(create(xp << 1 | 1, yp << 1 | 1, z + 1));
		return result;
	}

	@Override
	public boolean equals(Object o) {
		FoldIndex other = (FoldIndex) o;
		if (other == null)
			return false;
		else
			return this.fi == other.fi;
	}

	public String toString() {
		return "(fi=" + fi + ",x=" + x + ",y=" + y + ",z=" + z + ")";
	}

	public static boolean crossedChamber(Rectangle r1, Rectangle r2) {
		return (r1.x != r2.x) || (r1.y != r2.y) || ((r1.x + r1.width - 1) != (r2.x + r2.width - 1))
				|| ((r1.y + r1.height - 1) != (r2.y + r2.height - 1));
	}

	public static List<FoldIndex> covers(RectB viewRect, int z) {

		int side = FastMath.pow2x(z);

		int left = FastMath.bound(viewRect.getLeft(), -side / 2, side / 2);
		int right = FastMath.bound(viewRect.getRight(), -side / 2, side / 2);
		int up = FastMath.bound(viewRect.getTop(), -side / 2, side / 2);
		int down = FastMath.bound(viewRect.getBottom(), -side / 2, side / 2);

		List<FoldIndex> result = new ArrayList<FoldIndex>();
		StringBuilder sb = new StringBuilder();
		sb.append(viewRect.toString()).append(" covers:\n");
		for (int x = left; x <= right; x++)
			for (int y = up; y <= down; y++) {
				FoldIndex fi = create(x, y, z);
				result.add(fi);
				sb.append(fi.toString()).append(" ");
			}
		// Logger.getLogger(FoldIndex.class).debug(sb.toString());
		return result;
	}

	// public static List<FoldIndex> covers(RectC viewRect, int z) {
	//
	// int side = FastMath.pow2x(z);
	//
	// int left = FastMath.bound(viewRect.left(), -side / 2, side / 2);
	// int right = FastMath.bound(viewRect.right(), -side / 2, side / 2);
	// int up = FastMath.bound(viewRect.top(), -side / 2, side / 2);
	// int down = FastMath.bound(viewRect.bottom(), -side / 2, side / 2);
	//
	// List<FoldIndex> result = new ArrayList<FoldIndex>();
	//
	// for (int x = left; x <= right; x++)
	// for (int y = up; y <= down; y++) {
	// result.add(create(x, y, z));
	// }
	// return result;
	// }

	// public static List<FoldIndex> covers(Rectangle viewRect, int z) {
	//
	// int side = FastMath.pow2x(z);
	//
	// int left = FastMath.bound(viewRect.x, -side / 2, side / 2);
	// int right = FastMath.bound(viewRect.x + viewRect.width - 1, -side / 2,
	// side / 2);
	// int up = FastMath.bound(viewRect.y, -side / 2, side / 2);
	// int down = FastMath.bound(viewRect.y + viewRect.height - 1, -side / 2,
	// side / 2);
	//
	// List<FoldIndex> result = new ArrayList<FoldIndex>();
	//
	// for (int x = left; x <= right; x++)
	// for (int y = up; y <= down; y++) {
	// result.add(create(x, y, z));
	// }
	// return result;
	// }

	public static List<FoldIndex> getAllFiInPyramid(int zMax) {
		List<FoldIndex> fiAll = new ArrayList<FoldIndex>();
		for (int z = 0; z <= zMax; z++) {
			int side = sideLength(z);
			for (int i = 0; i < side; i++)
				for (int j = 0; j < side; j++) {
					fiAll.add(create(i - side / 2, j - side / 2, z));
				}
		}
		return fiAll;
	}

	public static List<FoldIndex> getAllFiOnLayer(int zMax) {
		List<FoldIndex> fiAll = new ArrayList<FoldIndex>();
		int side = sideLength(zMax);
		for (int i = 0; i < side; i++)
			for (int j = 0; j < side; j++) {
				fiAll.add(create(i - side / 2, j - side / 2, zMax));
			}
		return fiAll;
	}

}
