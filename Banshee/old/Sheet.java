package azura.banshee.nagaOld;

import java.awt.image.BufferedImage;

import common.algorithm.FastMath;
import common.collections.buffer.ZintBuffer;
import common.graphics.ImageUtil;

public class Sheet {

	public String[] md5 = new String[3];

	public int frameCount;
	private int rulerX, rulerY;
	private int width, tallest;

	public BufferedImage sheet;

	private int bound;

	public Sheet(int bound) {
		this.bound = bound;
		sheet = new BufferedImage(bound, bound, BufferedImage.TYPE_INT_ARGB);
	}

	public boolean append(Frame frame) {
		if (!heightAvailable(frame.height)) {
			return false;
		} else if (!widthAvailable(frame.width)) {
			if (tallest == 0)
				return false;
			else {
				rulerY += tallest;
				rulerX = 0;
				tallest = 0;
				return append(frame);
			}
		} else {
			copy(frame);
			return true;
		}
	}

	private int getHeight() {
		int height = rulerY + tallest;
		height = FastMath.getNextPowerOfTwo(height);
		return Math.max(4, height);
	}

	private void copy(Frame frame) {
		frameCount++;

		frame.xOnSheet = rulerX;
		frame.yOnSheet = rulerY;

		ImageUtil.copyPixels(frame.shrinkedImage, sheet, rulerX, rulerY);
		rulerX += frame.width;
		width = Math.max(width, rulerX);
		tallest = Math.max(tallest, frame.height);

		frame.shrinkedImage = null;
	}

	private boolean widthAvailable(int width) {
		return rulerX + width <= bound;
	}

	private boolean heightAvailable(int height) {
		return rulerY + height <= bound;
	}

	public void seal() {

		if (rulerY == 0) {
			width = rulerX;
		}
		width = FastMath.getNextPowerOfTwo(width);
		width = Math.max(4, width);
		sheet = sheet.getSubimage(0, 0, width, getHeight());
//		byte[][] atf = AtfUtil.image2atf(sheet);

//		md5[0] = GalFile.putData(atf[0]);
//		md5[1] = GalFile.putData(atf[1]);
//		md5[2] = GalFile.putData(atf[2]);

		// byte[] png = ImageUtil.encodePng(sheet);
		// FileUtil.write("./input/png/" + md5
		// + ".png", png);

		// sheet.flush();
		sheet = null;
	}

	public byte[] encode() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(width);
		zb.writeZint(getHeight());
		zb.writeUTFZ(md5[0]);
		zb.writeUTFZ(md5[1]);
		zb.writeUTFZ(md5[2]);
		return zb.toBytes();
	}
}
