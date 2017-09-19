package azura.banshee.zbase.station;

import azura.banshee.zbase.bus.WayDot45;

public class Pixel {
	int x, y;
	int gravityValue = 999999999;
	WayDot45 nearestStation;

	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Pixel() {
	}
}
