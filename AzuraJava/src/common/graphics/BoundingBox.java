package common.graphics;

import java.awt.Rectangle;

import common.collections.buffer.i.ZintWriterI;

public class BoundingBox extends Rectangle {

	private static final long serialVersionUID = 1L;

	private boolean initialized;
	
	public boolean initialized(){
		return initialized;
	}

	public void expand(int i, int j) {
		if (!initialized) {
			x = i;
			y = j;
			width = 1;
			height = 1;
			initialized = true;
		} else {
			int left = Math.min(x, i);
			int right = Math.max(x + width - 1, i);
			int up = Math.min(y, j);
			int down = Math.max(y + height - 1, j);
			x = left;
			y = up;
			width = right - left + 1;
			height = down - up + 1;
		}
	}

	public void write(ZintWriterI zb) {
		zb.writeZint(x);
		zb.writeZint(y);
		zb.writeZint(width);
		zb.writeZint(height);
	}
}
