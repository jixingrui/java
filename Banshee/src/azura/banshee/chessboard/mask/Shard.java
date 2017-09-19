package azura.banshee.chessboard.mask;

import java.awt.Point;
import java.util.ArrayList;

import common.collections.RectanglePlus;
import common.collections.bitset.abs.ABSet;
import common.collections.bitset.lbs.LBSet;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

/**
 * x,y,yFoot: global on positive coordinate
 */
@SuppressWarnings("serial")
public class Shard extends RectanglePlus implements BytesI {

	public int yFoot;
	/**
	 * relative to shard rectangle
	 */
	public LBSet lbs = new LBSet();

	public ArrayList<Point> box = new ArrayList<Point>();
	public final int color;

	public Shard(int color) {
		this.color = color;
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(x);
		zb.writeZint(y);
		zb.writeZint(width);
		zb.writeZint(height);
		zb.writeZint(yFoot);
		zb.writeBytesZ(lbs.toBytes());
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		throw new Error();
	}

	public void put(int xPix, int yPix) {
		super.expand(xPix, yPix);
		box.add(new Point(xPix, yPix));
	}

	public void seal() {
		ABSet abs = new ABSet();
		for (Point p : box) {
			p.x -= this.x;
			p.y -= this.y;
			int pos = p.y * width + p.x;
			abs.set(pos, true);
		}
		lbs.fromBytes(abs.toBytes());
	}
}
