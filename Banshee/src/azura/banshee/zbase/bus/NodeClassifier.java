package azura.banshee.zbase.bus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class NodeClassifier {

	final static int[][] nbrs = { { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 },
			{ 0, 1 }, { -1, 1 }, { -1, 0 }, { -1, -1 }, { 0, -1 } };

	static List<Point> toWhite = new ArrayList<>();
	private GeoMap grid;

	public static void classify(GeoMap gm) {
		NodeClassifier nc = new NodeClassifier();
		nc.classify_(gm);
	}

	private void classify_(GeoMap gm) {
		grid = gm;

		addNodes();
		shrink();
	}

	private void addNodes() {
		for (int y = 1; y + 1 < grid.height; y++) {
			for (int x = 1; x + 1 < grid.width; x++) {
				if (grid.skeletonGet(x, y)) {
					int trans = numTransitions(y, x);
					if (trans > 2)
						grid.junctionSet(x, y);
					else {
						boolean sol = solid(y, x);
						if (sol) {
							grid.junctionSet(x, y);
						} else {
							int n = numNeighbors(y, x);
							if (n > 4)
								grid.junctionSet(x, y);
							else if (n == 1)
								grid.terminalSet(x, y);
						}
					}
				}
			}
		}
	}

	private void shrink() {
		GeoMap gm = (GeoMap) grid;

		for (int s = gm.skeleton.nextSetBit(0); s >= 0; s = gm.skeleton
				.nextSetBit(s + 1)) {
			int x = s % gm.width;
			int y = s / gm.width;
			boolean p24 = grid.skeletonGet(x, y - 1)
					&& grid.skeletonGet(x + 1, y);
			if (p24)
				toWhite.add(new Point(x, y));
		}
		for (Point p : toWhite)
			grid.skeletonSet(p.x, p.y, false);
		toWhite.clear();

		for (int s = gm.skeleton.nextSetBit(0); s >= 0; s = gm.skeleton
				.nextSetBit(s + 1)) {
			int x = s % gm.width;
			int y = s / gm.width;
			boolean p46 = grid.skeletonGet(x + 1, y)
					&& grid.skeletonGet(x, y + 1);
			if (p46)
				toWhite.add(new Point(x, y));
		}
		for (Point p : toWhite)
			grid.skeletonSet(p.x, p.y, false);
		toWhite.clear();

		for (int s = gm.skeleton.nextSetBit(0); s >= 0; s = gm.skeleton
				.nextSetBit(s + 1)) {
			int x = s % gm.width;
			int y = s / gm.width;
			boolean p68 = grid.skeletonGet(x, y + 1)
					&& grid.skeletonGet(x - 1, y);
			if (p68)
				toWhite.add(new Point(x, y));
		}
		for (Point p : toWhite)
			grid.skeletonSet(p.x, p.y, false);
		toWhite.clear();

		for (int s = gm.skeleton.nextSetBit(0); s >= 0; s = gm.skeleton
				.nextSetBit(s + 1)) {
			int x = s % gm.width;
			int y = s / gm.width;
			boolean p28 = grid.skeletonGet(x, y - 1)
					&& grid.skeletonGet(x - 1, y);
			if (p28)
				toWhite.add(new Point(x, y));
		}
		for (Point p : toWhite)
			grid.skeletonSet(p.x, p.y, false);
		toWhite.clear();
	}

	private int numNeighbors(int y, int x) {
		int count = 0;
		for (int i = 0; i < nbrs.length - 1; i++)
			if (grid.skeletonGet(x + nbrs[i][0], y + nbrs[i][1]))
				count++;
		return count;
	}

	private int numTransitions(int y, int x) {
		int count = 0;
		for (int i = 0; i < nbrs.length - 1; i++)
			if (!grid.skeletonGet(x + nbrs[i][0], y + nbrs[i][1])) {
				if (grid.skeletonGet(x + nbrs[i + 1][0], y + nbrs[i + 1][1]))
					count++;
			}
		return count;
	}

	private boolean solid(int y, int x) {
		for (int i = 0; i < nbrs.length - 1; i++)
			if (grid.skeletonGet(x + nbrs[i][0], y + nbrs[i][1])) {
				if (grid.skeletonGet(x + nbrs[i + 1][0], y + nbrs[i + 1][1])) {
					int k = (i + 2) % 8;
					if (grid.skeletonGet(x + nbrs[k][0], y + nbrs[k][1])) {
						return true;
					}
				}
			}
		return false;
	}

}