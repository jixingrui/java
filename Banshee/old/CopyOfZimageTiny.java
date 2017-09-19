package azura.banshee.zimage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import azura.banshee.util.AtfUtil;
import azura.gallerid.Gal_File;
import azura.gallerid.Gal_Pack;

import common.algorithm.FastMath;
import common.collections.buffer.ByteSerializable;
import common.collections.buffer.ZintBuffer;
import common.graphics.ImageUtil;

public class CopyOfZimageTiny implements ByteSerializable {
	private int zMax;
	private BufferedImage[] tower;
	private BufferedImage tiny256;
	public byte[][] atf3;
	public String[] md5_3 = new String[3];

	public void load(BufferedImage source) {
		int maxSide = Math.max(source.getWidth(), source.getHeight());
		int next2n = FastMath.getNextPowerOfTwo(maxSide);
		zMax = FastMath.log2(next2n);
		if (zMax > 7)
			throw new IllegalArgumentException(
					"ZimageTiny: source must not exceed 128");
		tower = new BufferedImage[zMax + 1];

		int side = FastMath.pow2(zMax);
		BufferedImage image2n = drawToCenter(source, side);

		tower[zMax] = image2n;
		for (int z = zMax - 1; z >= 0; z--) {
			image2n = ImageUtil.scale(image2n, 0.5);
			tower[z] = image2n;
		}

		writeTo256();

		atf3 = AtfUtil.image2atf(tiny256);

		md5_3[0] = Gal_File.putData(atf3[0]);
		md5_3[1] = Gal_File.putData(atf3[1]);
		md5_3[2] = Gal_File.putData(atf3[2]);

		clear();
	}

	private void clear() {
		tower = null;
		tiny256 = null;
		atf3 = null;
	}

	private void writeTo256() {
		tiny256 = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = tiny256.createGraphics();

		for (int z = 0; z <= zMax; z++) {
			int x = FastMath.pow2(z);
			graphics2D.drawImage(tower[z], x, 0, null);
		}
		graphics2D.dispose();
	}

	void extractMd5(Gal_Pack gp) {
		gp.addSlave(md5_3[0]);
		gp.addSlave(md5_3[1]);
		gp.addSlave(md5_3[2]);
	}

	private static BufferedImage drawToCenter(BufferedImage in, int side) {
		final BufferedImage out = new BufferedImage(side, side,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics2D = out.createGraphics();
		graphics2D.drawImage(in, (side - in.getWidth()) / 2,
				(side - in.getHeight()) / 2, null);
		graphics2D.dispose();
		return out;
	}

	// public static BufferedImage resizeImage(final BufferedImage image,
	// double scale) {
	// int width = (int) (image.getWidth() * scale);
	// int height = (int) (image.getHeight() * scale);
	//
	// final BufferedImage bufferedImage = new BufferedImage(width, height,
	// BufferedImage.TYPE_INT_ARGB);
	// final Graphics2D graphics2D = bufferedImage.createGraphics();
	// graphics2D.setComposite(AlphaComposite.Src);
	// // below three lines are for RenderingHints for better image quality at
	// // cost of higher processing time
	// graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	// RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	// graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
	// RenderingHints.VALUE_RENDER_QUALITY);
	// graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	// graphics2D.drawImage(image, 0, 0, width, height, null);
	// graphics2D.dispose();
	// return bufferedImage;
	// }

	@Override
	public void fromBytes(byte[] data) {
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(zMax);
		zb.writeUTF(md5_3[0]);
		zb.writeUTF(md5_3[1]);
		zb.writeUTF(md5_3[2]);
		return zb.toBytes();
	}
}
