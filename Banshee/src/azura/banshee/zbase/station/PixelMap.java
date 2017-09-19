package azura.banshee.zbase.station;

import azura.banshee.zbase.Zbase;

public class PixelMap {
	private final Pixel[][] map;

	public PixelMap(int w, int h) {
		map = new Pixel[w][h];
	}

	public Pixel getPixel(int x, int y) {
		return map[x][y];
	}

	public void loadAll(Zbase zbase) {
	}
}
