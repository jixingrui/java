package azura.avalon.path;

import java.util.LinkedList;
import java.util.Queue;

import common.algorithm.FastMath;
import common.collections.Point;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintWriterI;
import common.logger.Trace;

public class PathStrider extends PathOfPoints {

	private volatile byte[] dump;

	private volatile BhStrider bh;
	public volatile int angle;

	public PathStrider(int x, int y, int angle) {
		this.angle = angle;
		bh = new BhStrider(x, y, x, y);
	}

	@Override
	public void clear() {
		dump = null;
		// Logger.getLogger(getClass()).debug("before clear:"+bh.toString());
		bh = new BhStrider(bh.getxNow(), bh.getyNow(), bh.getxNow(), bh.getyNow());
		// Logger.getLogger(getClass()).debug("after clear:"+bh.toString());
		super.clear();
	}

	public synchronized void jumpStart(int x, int y) {
		hopList.clear();
		angle = FastMath.atan2Degree(y - bh.getyNow(), x - bh.getxNow());
		bh = new BhStrider(x, y, x, y);
	}

	public void append(int xTo, int yTo) {
		hopList.add(new Point(xTo, yTo));
		dump = null;
	}

	public synchronized void append(Queue<Point> path_) {
		if (path_ == null || path_.isEmpty())
			return;

		hopList.addAll(path_);

		// if (bh.hasNext() == false) {
		// Point hop = hopList.poll();
		// bh = new BhStrider(bh.getxNow(), bh.getyNow(), hop.x, hop.y);
		// angle = FastMath.xy2Angle(hop.x - bh.getxNow(), hop.y -
		// bh.getyNow());
		// }

		dump = null;
	}

	/**
	 * thread safe
	 */
	public synchronized Point next(int stride) {
		stride = (stride > 0) ? stride : 1;

		int passed = bh.next(stride);
		if (passed == stride) {
			return new Point(bh.getxNow(), bh.getyNow());
		} else if (!hopList.isEmpty()) {
			dump = null;

			Point hop = hopList.poll();
			bh = new BhStrider(bh.getxNow(), bh.getyNow(), hop.x, hop.y);
			angle = FastMath.xy2Angle(hop.x - bh.getxNow(), hop.y - bh.getyNow());

			return next(stride - passed);
		} else if (passed > 0) {
			return new Point(bh.getxNow(), bh.getyNow());
		} else {
			return null;
		}

	}

	/**
	 * 
	 * @return zint(size)+{zint(x)+zint(y)}
	 */
	public synchronized byte[] toBytes() {
		if (dump == null) {
			// Logger.getLogger(getClass()).debug("dump hop list
			// size="+hopList.size());
			if (bh.hasNext() || hopList.isEmpty()) {
				ZintWriterI zb = new ZintBuffer();
				zb.writeZint(hopList.size() + 1);
				zb.writeZint(bh.getxDest());
				zb.writeZint(bh.getyDest());
				for (Point step : hopList) {
					zb.writeZint(step.x);
					zb.writeZint(step.y);
				}
				// Logger.getLogger(getClass()).debug("bh now included");
				dump = zb.toBytes();
			} else {
				// Logger.getLogger(getClass()).debug("bh now excluded");
				dump = super.toBytes();
			}
		}
		return dump;
	}

	public static void main(String[] args) {
		Point next = null;
		Queue<Point> track = trackGen(-10, -20, 8, 50, 50, 9999);
		Point start = track.peek();
		PathStrider path = new PathStrider(start.x, start.y, 0);
		path.append(track);

		for (int i = 0; i < 7; i++) {
			next = path.next(7);
			Trace.trace(next.x + "," + next.y);
		}
		Trace.trace("rewind");
		path.append(trackGen(0, 200, 8, 50, 50, 9999));

		while ((next = path.next(7)) != null) {
			Trace.trace(next.x + "," + next.y);
		}

	}

	public static Queue<Point> trackGen(int xStart, int yStart, int steps, int xStride, int yStride, int bound) {
		LinkedList<Point> result = new LinkedList<Point>();
		result.add(new Point(xStart, yStart));
		for (int i = 0; i < steps; i++) {
			xStart += xStride;
			yStart += yStride;
			// if(xStart<0 || xStart>=bound || yStart<0 || yStart>=bound)
			// break;

			result.add(new Point(xStart, yStart));
		}
		return result;
	}

	public boolean isEmpty() {
		return hopList.isEmpty() && (!bh.hasNext());
	}

}

// @Test
// public void jumpTest() {
// jumpBefore(10, 20);
//
// Assert.assertNotNull(next(1));
// Assert.assertNull(next(1));
// }
//
// /**
// * @next()!=null
// */
// public synchronized void jumpBefore(int x, int y) {
// hopList.clear();
// hopList.add(new Point(x, y));
// bh = null;
// }
