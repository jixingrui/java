package common.collections;

import java.awt.Rectangle;

import common.algorithm.FastMath;

public class RectC {
	public int xc, yc;
	private int width, height;
	private int leftHalf, rightHalf, topHalf, bottomHalf;

	private RectC() {
	}

	public RectC(int xc, int yc, int width, int height) {
		this.xc = xc;
		this.yc = yc;
		this.width = width;
		this.height = height;
		updateHalf();
	}

	private void updateHalf() {
		leftHalf = (int) Math.floor(width / 2d);
		rightHalf = (int) Math.ceil(width / 2d);
		topHalf = (int) Math.floor(height / 2d);
		bottomHalf = (int) Math.floor(height / 2d);
	}

	public int left() {
		return xc - leftHalf;
	}

	public int right() {
		return xc + rightHalf;
	}

	public int top() {
		return yc - topHalf;
	}

	public int bottom() {
		return yc + bottomHalf;
	}

	public Rectangle toRectangle() {
		Rectangle r = new Rectangle();
		r.x = left();
		r.y = top();
		r.width = width;
		r.height = height;
		return r;
	}

	public void fromRectangle(Rectangle r) {
		xc = r.x + r.width / 2;
		yc = r.y + r.height / 2;
		width = r.width;
		height = r.height;
		updateHalf();
	}

	public RectC shrink(int by) {
		double byD = by;
		int xcS = xc / by;
		int ycS = yc / by;
		int widthS = FastMath.ceilB(width / byD);
		int heightS = FastMath.ceilB(height / byD);
		RectC shrinked = new RectC(xcS, ycS, widthS, heightS);
		return shrinked;
	}

	@Override
	public boolean equals(Object obj) {
		RectC other = (RectC) obj;
		if (other == null)
			return false;

		return xc == other.xc && yc == other.yc && width == other.width && height == other.height;
	}

	public RectC clone() {
		return new RectC(xc, yc, width, height);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("xc=").append(xc).append(" yc=").append(yc).append(" w=").append(width).append(" h=").append(height);
		sb.append(" ; left=").append(left()).append(" right=").append(right()).append(" top=").append(top())
				.append(" bottom=").append(bottom());
		return sb.toString();
	}

	public boolean intersects(RectC r) {
		return this.toRectangle().intersects(r.toRectangle());
	}

	public RectC union(RectC r) {
		Rectangle u = this.toRectangle().union(r.toRectangle());
		RectC uc = new RectC();
		uc.fromRectangle(u);
		return uc;
	}

	public boolean contains(int x, int y) {
		return left() <= x && x <= right() && top() <= y && y <= bottom();
	}

}
