package azura.avalon.path;

import java.awt.Point;

import common.logger.Trace;

public final class Bresenham {

	private int xStart, yStart, xEnd, yEnd;
	private int dx, dy, stepx, stepy;
	private int fraction;

	public void reset(int xStart, int yStart, int xEnd, int yEnd) {
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
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

	public Point next() {
		if (dx > dy) {
			if (xStart != xEnd) {
				if (fraction >= 0) {
					yStart += stepy;
					fraction -= dx;
				}
				xStart += stepx;
				fraction += dy;
				return new Point(xStart, yStart);
			}
		} else {
			if (yStart != yEnd) {
				if (fraction >= 0) {
					xStart += stepx;
					fraction -= dy;
				}
				yStart += stepy;
				fraction += dx;
				return new Point(xStart, yStart);
			}
		}
		return null;
	}

	public void basicTest() {
		Trace.trace(next());
	}
}
