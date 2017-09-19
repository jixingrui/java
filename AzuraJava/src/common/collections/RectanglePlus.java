package common.collections;

import java.awt.Rectangle;

@SuppressWarnings("serial")
public class RectanglePlus extends Rectangle {

	public RectanglePlus() {
	}

	public RectanglePlus(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public boolean expand(int x, int y) {
		boolean mod = false;
		if (width == 0) {
			this.x = x;
			this.width = 1;
			mod=true;
		} else if (x < getLeft()) {
			setLeft(x);
			mod = true;
		} else if (x > getRight()) {
			setRight(x);
			mod = true;
		}
		if (height == 0) {
			this.y = y;
			this.height = 1;
			mod=true;
		} else if (y < getCeiling()) {
			setCeiling(y);
			mod = true;
		} else if (y > getFloor()) {
			setFloor(y);
			mod = true;
		}
		return mod;
	}

	public int getLeft() {
		return x;
	}

	public void setLeft(int value) {
		int right = getRight();
		x = value;
		setRight(right);
	}

	public int getCeiling() {
		return y;
	}

	public void setCeiling(int value) {
		int bottom = getFloor();
		y = value;
		setFloor(bottom);
	}

	/**
	 * inclusive
	 */
	public int getRight() {
		return x + width - 1;
	}

	/**
	 * inclusive
	 */
	public void setRight(int value) {
		width = value - x + 1;
	}

	/**
	 * inclusive
	 */
	public int getFloor() {
		return y + height - 1;
	}

	/**
	 * inclusive
	 */
	public void setFloor(int value) {
		height = value - y + 1;
	}

	public RectanglePlus clone() {
		return new RectanglePlus(x, y, width, height);
	}

	@Override
	public String toString() {
		return "left=" + getLeft() + " right=" + getRight() + " ceiling="
				+ getCeiling() + " floor=" + getFloor() + " width=" + width
				+ " height=" + height;
	}
}
