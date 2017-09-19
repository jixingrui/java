package azura.banshee.zbase.bus;

import java.awt.image.BufferedImage;
import java.util.BitSet;

import common.collections.bitset.abs.ABSet;

public class GeoMap {
	public int width, height;

	public BitSet skeleton = new BitSet();
	public ABSet terminal = new ABSet();
	public ABSet junction = new ABSet();
	public ABSet folding = new ABSet();
	public ABSet bottleNeck = new ABSet();

	public void skeletonSet(int x, int y, boolean value) {
		skeleton.set(y * width + x, value);
	}

	public boolean skeletonGet(int x, int y) {
		return skeleton.get(y * width + x);
	}

	public boolean junctionGet(int x, int y) {
		return junction.get(y * width + x);
	}

	public void junctionSet(int x, int y) {
		junction.set(y * width + x, true);
	}

	public void terminalSet(int x, int y) {
		terminal.set(y * width + x, true);
	}

	public boolean terminalGet(int x, int y) {
		return terminal.get(y * width + x);
	}

	public void foldingSet(int x, int y) {
		folding.set(y * width + x, true);
	}

	public boolean foldingGet(int x, int y) {
		return folding.get(y * width + x);
	}

	public void bottleNeckSet(int x, int y) {
		bottleNeck.set(y * width + x, true);
	}

	public boolean bottleNeckGet(int x, int y) {
		return bottleNeck.get(y * width + x);
	}

	public void draw(BufferedImage bi) {
		int dx = -width / 2 + bi.getWidth() / 2;
		int dy = -height / 2 + bi.getHeight() / 2;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (junctionGet(x, y))
					bi.setRGB(x + dx, y + dy, 0xffff0000);
				else if (terminalGet(x, y))
					bi.setRGB(x + dx, y + dy, 0xff0000ff);
				else if (foldingGet(x, y))
					bi.setRGB(x + dx, y + dy, 0xffff8800);
				else if (skeletonGet(x, y))
					bi.setRGB(x + dx, y + dy, 0xff00ffff);
				else if (bottleNeckGet(x, y))
					bi.setRGB(x + dx, y + dy, 0xff000000);
			}
		}
	}
}
