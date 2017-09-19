package azura.avalon.path;

import java.util.LinkedList;
import java.util.Queue;

import common.collections.Point;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class PathOfPoints implements BytesI {
	public volatile Queue<Point> hopList = new LinkedList<Point>();

	public void clear() {
		hopList.clear();
	}
	
	/**
	 * 
	 * @return zint(size)+{zint(x)+zint(y)}
	 */
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(hopList.size());
		hopList.forEach(p -> {
			zb.writeZint(p.x);
			zb.writeZint(p.y);
		});
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		hopList.clear();
		ZintBuffer zb = new ZintBuffer(bytes);
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			Point p = new Point(zb.readZint(), zb.readZint());
			hopList.add(p);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		hopList.forEach(p -> {
			sb.append("(").append(p.x).append(",").append(p.y).append(")");
		});
		return sb.toString();
	}
}
