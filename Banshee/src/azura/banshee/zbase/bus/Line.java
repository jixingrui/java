package azura.banshee.zbase.bus;

import java.util.ArrayList;
import java.util.List;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Line implements BytesI {
	WayDot45 start, end;
	List<WayDot45> between = new ArrayList<>();

	public Line(WayDot45 start) {
		this.start = start;
	}

	public void end(WayDot45 end) {
		this.end = end;
		if (start == end)
			throw new Error();
	}

	public boolean removeDup() {
		if (start.xy > end.xy) {
			start.lines.remove(this);
			start = null;
			between = null;
			return true;
		} else
			return false;
	}

	@Override
	public void fromBytes(byte[] bytes) {
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeInt(start.xy);
		zb.writeInt(end.xy);
		return zb.toBytes();
	}

}
