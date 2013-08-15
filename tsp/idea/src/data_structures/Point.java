package data_structures;

/**
 * User: iiotep9huy
 * Date: 8/15/13
 * Time: 10:19 AM
 * Project: TSP
 */
public class Point {

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point() {
		this.x = 0;
		this.y = 0;
	}

	static public double distance(Point lhs, Point rhs) {
		return Math.sqrt((lhs.x - rhs.x) * (lhs.x - rhs.x) + (lhs.y - rhs.y) * (lhs.y - rhs.y));
	}

	public int x, y;
}
