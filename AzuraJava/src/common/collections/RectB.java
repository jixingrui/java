package common.collections;

import java.awt.Rectangle;

public class RectB {
	// right is inclusive?
	private int left, right, top, bottom;

	private RectB() {
	}

	public RectB(int xc, int yc, int width, int height) {
		setXC(xc);
		setYc(yc);
		setWidth(width);
		setHeight(height);
	}

	// ====================== getter ===================
	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}

	public int getTop() {
		return top;
	}

	public int getBottom() {
		return bottom;
	}

	public int getXC() {
		return (left + right) / 2;
	}

	public int getYC() {
		return (bottom + top) / 2;
	}

	public int getWidth() {
		return right - left + 1;
	}

	public int getHeight() {
		return bottom - top + 1;
	}

	// =================== setter ===============
	public void setXC(int xc) {
		int w2 = Math.floorDiv(getWidth(), 2);
		left = xc - w2;
		right = xc + w2;
	}

	public void setYc(int yc) {
		int h2 = Math.floorDiv(getHeight(), 2);
		top = yc - h2;
		bottom = yc + h2;
	}

	public void setWidth(int width) {
		int xc = getXC();
		int w2 = Math.floorDiv(width, 2);
		left = xc - w2;
		right = xc + w2;
	}

	public void setHeight(int height) {
		int yc = getYC();
		int h2 = Math.floorDiv(height, 2);
		top = yc - h2;
		bottom = yc + h2;
	}

	// ================== format ==================
	private Rectangle toRectangle() {
		Rectangle r = new Rectangle();
		r.x = left;
		r.y = top;
		r.width = getWidth();
		r.height = getHeight();
		return r;
	}

	// private void fromRectangle(Rectangle r) {
	// left = r.x;
	// top = r.y;
	// right = r.x + r.width;
	// bottom = r.y + r.height;
	// }

	public RectB shrink(int by) {
		RectB small = new RectB();
		small.left = Math.floorDiv(left, by);
		small.right = Math.floorDiv(right, by);
		small.top = Math.floorDiv(top, by);
		small.bottom = Math.floorDiv(bottom, by);
		return small;
	}

	@Override
	public boolean equals(Object obj) {
		RectB other = (RectB) obj;
		if (other == null)
			return false;

		return left == other.left && right == other.right && top == other.top && bottom == other.bottom;
	}

	public RectB clone() {
		RectB c = new RectB();
		c.left = left;
		c.right = right;
		c.top = top;
		c.bottom = bottom;
		return c;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		// sb.append("xc=").append(xc).append(" yc=").append(yc).append("
		// w=").append(width).append(" h=").append(height);
		sb.append("left=").append(left).append(" right=").append(right).append(" top=").append(top).append(" bottom=")
				.append(bottom);
		return sb.toString();
	}

	public boolean intersects(RectB r) {
		return this.toRectangle().intersects(r.toRectangle());
	}

	public RectB union(RectB r) {
		RectB result = new RectB();
		result.left = Math.min(this.left, r.left);
		result.right = Math.max(this.right, r.right);
		result.top = Math.min(this.top, r.top);
		result.bottom = Math.max(this.bottom, r.bottom);
		return result;
	}

	public boolean contains(int x, int y) {
		return left <= x && x <= right && top <= y && y <= bottom;
	}

}
