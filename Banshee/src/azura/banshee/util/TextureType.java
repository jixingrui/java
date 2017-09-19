package azura.banshee.util;

import java.awt.image.BufferedImage;

public enum TextureType {
	Init, Empty, Alpha, Solid;

	public static TextureType check(BufferedImage slice) {
		return check(slice, 0, 0, slice.getWidth(), slice.getHeight());
	}

	public static TextureType check(BufferedImage slice, int xFrom, int yFrom,
			int width, int height) {
		boolean isEmpty = true;
		boolean hasAlpha = false;
		// int w = slice.getWidth();
		// int h = slice.getHeight();
		int w=xFrom+width;
		int h=yFrom+height;
		for (int i = xFrom; i < w; i++)
			for (int j = yFrom; j < h; j++) {
				int color = slice.getRGB(i, j);
				int alpha = color >>> 24;
				if (alpha != 0) {
					isEmpty = false;
				}
				if (alpha != 0xff) {
					hasAlpha = true;
					if (!isEmpty)
						return TextureType.Alpha;
				}
			}
		if (isEmpty)
			return TextureType.Empty;
		if (hasAlpha)
			return TextureType.Alpha;
		return TextureType.Solid;
	}
}
