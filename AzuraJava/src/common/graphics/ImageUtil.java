package common.graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;

import common.collections.RectanglePlus;

public class ImageUtil {
	public static final Logger log = Logger.getLogger(ImageUtil.class);

	/**
	 * @param fileName
	 * @param colors
	 *            <=255
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void pngquanti(String fileName, int colors) throws IOException, InterruptedException {
		colors = Math.min(255, colors);
		String cmd = "pngquant/pngquanti.exe -force -ordered -ext .png " + colors + " " + fileName;

		Runtime rn = Runtime.getRuntime();
		Process child = rn.exec(cmd);
		child.waitFor();
	}

	public static BufferedImage getSubImage(BufferedImage src, int x, int y, int width, int height) {
		width = Math.max(width, 1);
		height = Math.max(height, 1);
		BufferedImage sub = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		sub.getGraphics().drawImage(src.getSubimage(x, y, width, height), 0, 0, null);
		return sub;
	}

	public static void copyPixels(final BufferedImage src, final BufferedImage dst, final int xDest, final int yDest) {

		int[] data = new int[src.getWidth() * src.getHeight()];
		src.getRGB(0, 0, src.getWidth(), src.getHeight(), data, 0, src.getWidth());
		dst.setRGB(xDest, yDest, src.getWidth(), src.getHeight(), data, 0, src.getWidth());
	}

	public static BufferedImage duplicate(BufferedImage image) {
		if (image == null)
			throw new NullPointerException();

		int type = BufferedImage.TYPE_INT_RGB;
		if (image.getColorModel().hasAlpha()) {
			type = BufferedImage.TYPE_INT_ARGB;
		}

		BufferedImage j = new BufferedImage(image.getWidth(), image.getHeight(), type);
		j.setData(image.getData());
		return j;
	}

	public static BufferedImage decodeJpng(byte[] jpg) {
		return decodePng(jpg);
	}

	public static BufferedImage decodePng(byte[] png) {
		return decodeBitmap(png);
	}

	public static BufferedImage decodeBitmap(byte[] png_jpg) {
		InputStream input = new ByteArrayInputStream(png_jpg);
		BufferedImage image = null;
		try {
			image = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public static byte[] encodePng(BufferedImage image) {
		BufferedImage argb = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		argb.getGraphics().drawImage(image, 0, 0, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageEncoder encoder = ImageCodec.createImageEncoder("PNG", baos, null);
		try {
			encoder.encode(argb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] result = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param quality
	 *            0-1
	 */
	public static byte[] encodeJpg(BufferedImage image, float quality) {
		BufferedImage rgb = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		rgb.getGraphics().drawImage(image, 0, 0, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JPEGEncodeParam param = new JPEGEncodeParam();
		param.setQuality(quality);
		ImageEncoder encoder = ImageCodec.createImageEncoder("JPEG", baos, param);
		try {
			encoder.encode(rgb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] result = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean colorDifferent(int color1, int color2) {
		int alpha1 = color1 >>> 24;
		int alpha2 = color2 >>> 24;
		if (alpha1 < 32 && alpha2 < 32)
			// both none visible
			return false;
		else
			return colorDistAlpha(color1, color2) > 32 || colorDistCIE76(color1, color2) > 8
					|| colorDistPgras(color1, color2) > 32;
	}

	public static int colorDistAlpha(int color1, int color2) {
		int alpha1 = color1 >>> 24;
		int alpha2 = color2 >>> 24;
		return Math.abs(alpha1 - alpha2);
	}

	/**
	 * distance>2.3: noticeable; distance>23: obvious
	 * 
	 * @return distance
	 */
	public static double colorDistCIE76(int color1, int color2) {
		int[] lab1 = rgb2lab(color1);
		int[] lab2 = rgb2lab(color2);

		int d0 = lab1[0] - lab2[0];
		int d1 = lab1[1] - lab2[1];
		int d2 = lab1[2] - lab2[2];

		return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	public static double colorDistPgras(int color1, int color2) {
		int r1 = (color1 & 0xff0000) >>> 16;
		int g1 = (color1 & 0xff00) >>> 8;
		int b1 = color1 & 0xff;
		int r2 = (color2 & 0xff0000) >>> 16;
		int g2 = (color2 & 0xff00) >>> 8;
		int b2 = color2 & 0xff;

		double rmean = (r1 + r2) / 2;
		int r = r1 - r2;
		int g = g1 - g2;
		int b = b1 - b2;
		double weightR = 2 + rmean / 256;
		double weightG = 4.0;
		double weightB = 2 + (255 - rmean) / 256;
		return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
	}

	public static double colorDistRGB(int color1, int color2) {
		color1 &= 0xffffff;
		color2 &= 0xffffff;

		int r1 = (color1 & 0xff0000) >>> 16;
		int g1 = (color1 & 0xff00) >>> 8;
		int b1 = color1 & 0xff;

		int r2 = (color2 & 0xff0000) >>> 16;
		int g2 = (color2 & 0xff00) >>> 8;
		int b2 = color2 & 0xff;

		return Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
	}

	public static double colorDist2000(int color1, int color2) {
		int alpha1 = color1 >>> 24;
		int alpha2 = color2 >>> 24;
		if ((alpha1 == 0 && alpha2 != 0) || (alpha1 != 0 && alpha2 == 0))
			return 100;

		int[] lab1 = rgb2lab(color1);
		int[] lab2 = rgb2lab(color2);

		return deltaE2000(lab1, lab2);
	}

	private static double deltaE2000(int[] lab1, int[] lab2) {
		int L1 = lab1[0];
		int a1 = lab1[1];
		int b1 = lab1[2];

		int L2 = lab2[0];
		int a2 = lab2[1];
		int b2 = lab2[2];

		// Cab = sqrt(a^2 + b^2)
		double Cab1 = Math.sqrt(a1 * a1 + b1 * b1);
		double Cab2 = Math.sqrt(a2 * a2 + b2 * b2);

		// CabAvg = (Cab1 + Cab2) / 2
		double CabAvg = (Cab1 + Cab2) / 2;

		// G = 1 + (1 - sqrt((CabAvg^7) / (CabAvg^7 + 25^7))) / 2
		double CabAvg7 = Math.pow(CabAvg, 7);
		double G = 1 + (1 - Math.sqrt(CabAvg7 / (CabAvg7 + 6103515625.0))) / 2;

		// ap = G * a
		double ap1 = G * a1;
		double ap2 = G * a2;

		// Cp = sqrt(ap^2 + b^2)
		double Cp1 = Math.sqrt(ap1 * ap1 + b1 * b1);
		double Cp2 = Math.sqrt(ap2 * ap2 + b2 * b2);

		// CpProd = (Cp1 * Cp2)
		double CpProd = Cp1 * Cp2;

		// hp1 = atan2(b1, ap1)
		double hp1 = Math.atan2(b1, ap1);
		// ensure hue is between 0 and 2pi
		if (hp1 < 0) {
			// hp1 = hp1 + 2pi
			hp1 += 6.283185307179586476925286766559;
		}

		// hp2 = atan2(b2, ap2)
		double hp2 = Math.atan2(b2, ap2);
		// ensure hue is between 0 and 2pi
		if (hp2 < 0) {
			// hp2 = hp2 + 2pi
			hp2 += 6.283185307179586476925286766559;
		}

		// dL = L2 - L1
		double dL = L2 - L1;

		// dC = Cp2 - Cp1
		double dC = Cp2 - Cp1;

		// computation of hue difference
		double dhp = 0.0;
		// set hue difference to zero if the product of chromas is zero
		if (CpProd != 0) {
			// dhp = hp2 - hp1
			dhp = hp2 - hp1;
			if (dhp > Math.PI) {
				// dhp = dhp - 2pi
				dhp -= 6.283185307179586476925286766559;
			} else if (dhp < -Math.PI) {
				// dhp = dhp + 2pi
				dhp += 6.283185307179586476925286766559;
			}
		}

		// dH = 2 * sqrt(CpProd) * sin(dhp / 2)
		double dH = 2 * Math.sqrt(CpProd) * Math.sin(dhp / 2);

		// weighting functions
		// Lp = (L1 + L2) / 2 - 50
		double Lp = (L1 + L2) / 2 - 50;

		// Cp = (Cp1 + Cp2) / 2
		double Cp = (Cp1 + Cp2) / 2;

		// average hue computation
		// hp = (hp1 + hp2) / 2
		double hp = (hp1 + hp2) / 2;

		// identify positions for which abs hue diff exceeds 180 degrees
		if (Math.abs(hp1 - hp2) > Math.PI) {
			// hp = hp - pi
			hp -= Math.PI;
		}
		// ensure hue is between 0 and 2pi
		if (hp < 0) {
			// hp = hp + 2pi
			hp += 6.283185307179586476925286766559;
		}

		// LpSqr = Lp^2
		double LpSqr = Lp * Lp;

		// Sl = 1 + 0.015 * LpSqr / sqrt(20 + LpSqr)
		double Sl = 1 + 0.015 * LpSqr / Math.sqrt(20 + LpSqr);

		// Sc = 1 + 0.045 * Cp
		double Sc = 1 + 0.045 * Cp;

		// T = 1 - 0.17 * cos(hp - pi / 6) +
		// + 0.24 * cos(2 * hp) +
		// + 0.32 * cos(3 * hp + pi / 30) -
		// - 0.20 * cos(4 * hp - 63 * pi / 180)
		double hphp = hp + hp;
		double T = 1 - 0.17 * Math.cos(hp - 0.52359877559829887307710723054658) + 0.24 * Math.cos(hphp)
				+ 0.32 * Math.cos(hphp + hp + 0.10471975511965977461542144610932)
				- 0.20 * Math.cos(hphp + hphp - 1.0995574287564276334619251841478);

		// Sh = 1 + 0.015 * Cp * T
		double Sh = 1 + 0.015 * Cp * T;

		// deltaThetaRad = (pi / 3) * e^-(36 / (5 * pi) * hp - 11)^2
		double powerBase = hp - 4.799655442984406;
		double deltaThetaRad = 1.0471975511965977461542144610932 * Math.exp(-5.25249016001879 * powerBase * powerBase);

		// Rc = 2 * sqrt((Cp^7) / (Cp^7 + 25^7))
		double Cp7 = Math.pow(Cp, 7);
		double Rc = 2 * Math.sqrt(Cp7 / (Cp7 + 6103515625.0));

		// RT = -sin(delthetarad) * Rc
		double RT = -Math.sin(deltaThetaRad) * Rc;

		// de00 = sqrt((dL / Sl)^2 + (dC / Sc)^2 + (dH / Sh)^2 + RT * (dC / Sc)
		// * (dH / Sh))
		double dLSl = dL / Sl;
		double dCSc = dC / Sc;
		double dHSh = dH / Sh;
		return Math.sqrt(dLSl * dLSl + dCSc * dCSc + dHSh * dHSh + RT * dCSc * dHSh);
	}

	private static int[] rgb2lab(int rgb) {
		// http://www.brucelindbloom.com

		int[] lab = new int[3];

		int R = rgb & 0xff0000 >> 16;
		int G = rgb & 0xff00 >> 8;
		int B = rgb & 0xff;

		float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
		float Ls, as, bs;
		float eps = 216.f / 24389.f;
		float k = 24389.f / 27.f;

		float Xr = 0.964221f; // reference white D50
		float Yr = 1.0f;
		float Zr = 0.825211f;

		// RGB to XYZ
		r = R / 255.f; // R 0..1
		g = G / 255.f; // G 0..1
		b = B / 255.f; // B 0..1

		// assuming sRGB (D65)
		if (r <= 0.04045)
			r = r / 12;
		else
			r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

		if (g <= 0.04045)
			g = g / 12;
		else
			g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

		if (b <= 0.04045)
			b = b / 12;
		else
			b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

		X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
		Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
		Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

		// XYZ to Lab
		xr = X / Xr;
		yr = Y / Yr;
		zr = Z / Zr;

		if (xr > eps)
			fx = (float) Math.pow(xr, 1 / 3.);
		else
			fx = (float) ((k * xr + 16.) / 116.);

		if (yr > eps)
			fy = (float) Math.pow(yr, 1 / 3.);
		else
			fy = (float) ((k * yr + 16.) / 116.);

		if (zr > eps)
			fz = (float) Math.pow(zr, 1 / 3.);
		else
			fz = (float) ((k * zr + 16.) / 116);

		Ls = (116 * fy) - 16;
		as = 500 * (fx - fy);
		bs = 200 * (fy - fz);

		lab[0] = (int) (2.55 * Ls + .5);
		lab[1] = (int) (as + .5);
		lab[2] = (int) (bs + .5);

		return lab;
	}

	public static int colorReverse(int argb) {
		// int a = argb >>> 24;
		int a = 0xff;
		int r = argb & 0xff0000 >> 16;
		int g = argb & 0xff00 >> 8;
		int b = argb & 0xff;
		if (r > g && g > b) {
			b = (256 - b) % 256;
		} else if (g > b && b > r) {
			r = (256 - r) % 256;
		} else {
			g = (256 - g) % 256;
		}
		int reverse = a << 24 | r << 16 | g << 8 | b;
		return reverse;
	}

	public static Color colorContrast(int color) {
		return (color & 0xffffff) > 0xffffff / 2 ? Color.BLACK : Color.WHITE;
	}

	public static void drawBigDot(BufferedImage image, int color, int x, int y) {
		Graphics g = image.getGraphics();
		g.setColor(new Color(color));
		g.fillOval(x - 16, y - 16, 32, 32);
		g.setColor(Color.BLACK);
		g.fillOval(x - 12, y - 12, 24, 24);
		g.setColor(Color.WHITE);
		g.fillOval(x - 5, y - 5, 10, 10);
		g.dispose();
	}

	/**
	 * @param source
	 *            source image with alpha
	 * @return non transparent rectangle
	 */
	public static RectanglePlus getBoundingBox(BufferedImage source) {
		int left = 0;
		int right = source.getWidth() - 1;
		int top = 0;
		int bottom = source.getHeight() - 1;
		leftExit: for (; left <= right; left++)
			for (int j = top; j <= bottom; j++) {
				if (isPixelVisible(source.getRGB(left, j)))
					break leftExit;
			}
		rightExit: for (; left <= right; right--)
			for (int j = top; j <= bottom; j++) {
				if (isPixelVisible(source.getRGB(right, j)))
					break rightExit;
			}
		topExit: for (; top <= bottom; top++)
			for (int i = left; i <= right; i++) {
				if (isPixelVisible(source.getRGB(i, top)))
					break topExit;
			}
		bottomExit: for (; top <= bottom; bottom--)
			for (int i = left; i <= right; i++) {
				if (isPixelVisible(source.getRGB(i, bottom)))
					break bottomExit;
			}
		return new RectanglePlus(left, top, right - left + 1, bottom - top + 1);
	}

	public static RectanglePlus getBoundingBox4x4(BufferedImage source) {
		int left = 0;
		int right = source.getWidth() - 1;
		int top = 0;
		int bottom = source.getHeight() - 1;
		leftExit: for (; left <= right - 4; left++)
			for (int j = top; j <= bottom; j++) {
				if (isPixelVisible(source.getRGB(left, j)))
					break leftExit;
			}
		rightExit: for (; left <= right - 4; right--)
			for (int j = top; j <= bottom; j++) {
				if (isPixelVisible(source.getRGB(right, j)))
					break rightExit;
			}
		topExit: for (; top <= bottom - 4; top++)
			for (int i = left; i <= right; i++) {
				if (isPixelVisible(source.getRGB(i, top)))
					break topExit;
			}
		bottomExit: for (; top <= bottom - 4; bottom--)
			for (int i = left; i <= right; i++) {
				if (isPixelVisible(source.getRGB(i, bottom)))
					break bottomExit;
			}
		return new RectanglePlus(left, top, right - left + 1, bottom - top + 1);
	}

	private static boolean isPixelVisible(int color) {
		// Trace.trace(Hex.getHex(color));
		int alpha = color & 0xff000000;
		return alpha != 0;
	}

	// private static BufferedImage toEven(BufferedImage image) {
	// int wOdd = 0;
	// int hOdd = 0;
	// if (FastMath.isOdd(image.getWidth()))
	// wOdd = 1;
	// if (FastMath.isOdd(image.getHeight()))
	// hOdd = 1;
	//
	// if ((wOdd + hOdd) == 0) {
	// return image;
	// } else {
	// BufferedImage even = new BufferedImage(image.getWidth() + wOdd,
	// image.getHeight() + hOdd, BufferedImage.TYPE_INT_ARGB);
	// copyPixels(image, even, 0, 0);
	// return even;
	// }
	// }

	public static BufferedImage scale(final BufferedImage image, double scale) {

		// BufferedImage image = toEven(source);

		// ======================= why floor before? ===================
		int width = (int) Math.round(scale * image.getWidth());
		int height = (int) Math.round(scale * image.getHeight());

		// log.info("height from " + image.getHeight() + " to " + height);

		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setComposite(AlphaComposite.Src);
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		graphics2D.dispose();
		return bufferedImage;
	}

	public static BufferedImage newImage(int w, int h, Color fillColor) {
		BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = result.getGraphics();
		g.setColor(fillColor);
		g.fillRect(0, 0, w, h);
		g.dispose();
		return result;
	}

	public static void writePng(BufferedImage img, String path) throws IOException {
		ImageIO.write(img, "png", new File(path));
	}
}
