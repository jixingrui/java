package common.collections;

public class Point implements Comparable<Point> {
	public int x, y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point() {
	}

	@Override
	public int compareTo(Point other) {
		if (this.x > other.x)
			return 1;
		if (this.x < other.x)
			return -1;
		if (this.y > other.y)
			return 1;
		if (this.y < other.y)
			return -1;
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		Point other = (Point) obj;
		return this.x == other.x && this.y == other.y;
	}
}
