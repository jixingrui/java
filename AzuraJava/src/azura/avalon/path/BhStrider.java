package azura.avalon.path;

import common.logger.Trace;

public final class BhStrider {

	private int xNow, yNow, xDest, yDest;

	private int dx, dy, stepx, stepy;
	private int fraction;

	public BhStrider(int xStart, int yStart, int xEnd, int yEnd) {
		this.xNow = xStart;
		this.yNow = yStart;
		this.xDest = xEnd;
		this.yDest = yEnd;
		dy = yEnd - yStart;
		dx = xEnd - xStart;
		if (dy < 0) {
			dy = -dy;
			stepy = -1;
		} else {
			stepy = 1;
		}
		if (dx < 0) {
			dx = -dx;
			stepx = -1;
		} else {
			stepx = 1;
		}
		dy <<= 1;
		dx <<= 1;

		if (dx > dy)
			fraction = dy - (dx >> 1);
		else
			fraction = dx - (dy >> 1);
	}

	public boolean hasNext() {
		return xNow != xDest || yNow != yDest;
	}

	/**
	 * @return passed
	 */
	public int next(int stride) {
		int passed = -1;
		if (dx > dy) {
			while (++passed < stride && xNow != xDest) {
				if (fraction >= 0) {
					yNow += stepy;
					fraction -= dx;
				}
				xNow += stepx;
				fraction += dy;
			}
		} else {
			while (++passed < stride && yNow != yDest) {
				if (fraction >= 0) {
					xNow += stepx;
					fraction -= dy;
				}
				yNow += stepy;
				fraction += dx;
			}
		}
		return passed;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(").append(xNow).append(",").append(yNow).append(")(").append(xDest).append(",").append(yDest)
				.append(")");
		return sb.toString();
	}

	public static void testGo() {
		BhStrider bs = new BhStrider(100, 100, 100, 110);

		int stride = 7;
		int passed = bs.next(stride);
		while (passed == stride) {
			Trace.trace(bs.getxNow() + "," + bs.getyNow() + "," + passed);
			passed = bs.next(stride);
		}
		Trace.trace(bs.getxNow() + "," + bs.getyNow() + "," + passed);
	}

	public int getxDest() {
		return xDest;
	}

	public int getyDest() {
		return yDest;
	}

	public int getxNow() {
		return xNow;
	}

	public int getyNow() {
		return yNow;
	}
}
