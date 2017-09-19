package azura.banshee.zbase;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.BitSet;

import azura.banshee.chessboard.fi.TileFi;
import common.algorithm.FastMath;
import common.collections.bitset.lbs.LBSet;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class ZbaseTile extends TileFi<ZbaseTile> implements BytesI {

	private LBSet targetPos = new LBSet();
	private boolean allTarget = false;
	private boolean allNot = true;

	// cache
	private BitSet bs;

	public ZbaseTile(int fi, Zbase pyramid) {
		super(fi, pyramid);
	}

	public void load(BufferedImage image, Color target, boolean reverse) {

		int c = target.getRGB() & 0xffffff;
		for (int i = 0; i < pyramid.tileSide; i++)
			for (int j = 0; j < pyramid.tileSide; j++) {
				int color = image.getRGB(i, j) & 0xffffff;
				boolean isTarget = (color == c) ^ reverse;
				allTarget &= isTarget;
				allNot &= !isTarget;
				targetPos.push(isTarget);
			}
	}

	public boolean canWalk(int x, int y) {
		if (allTarget)
			return true;
		else if (allNot)
			return false;
		else {
			if (bs == null)
				bs = targetPos.toBitSet();

			int idx = xyToIdx(x, y);
			return bs.get(idx);
		}
	}

	private int xyToIdx(int x, int y) {
		return x * pyramid.tileSide + y;
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb = new ZintBuffer(data);
		allTarget = zb.readBoolean();
		allNot = zb.readBoolean();
		targetPos.fromBytes(zb.readBytesZ());
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBoolean(allTarget);
		zb.writeBoolean(allNot);
		zb.writeBytesZ(targetPos.toBytes());
		return zb.toBytes();
	}

	/**
	 * @param canvas
	 *            output image
	 * @param paint
	 *            color
	 * @param dxg
	 *            delta x of center on zMax
	 * @param dyg
	 *            delta y of center on zMax
	 * @param dz
	 *            scale down
	 */
	public void draw(BufferedImage canvas, Color paint, int dxg, int dyg, int dz) {
		int scale = FastMath.pow2x(dz);
		float dx = dxg + xp * pyramid.tileSide - FastMath.pow2x(z) * pyramid.tileSide / 2
				+ canvas.getWidth() * scale / 2;
		float dy = dyg + yp * pyramid.tileSide - FastMath.pow2x(z) * pyramid.tileSide / 2
				+ canvas.getHeight() * scale / 2;

		if (allTarget) {
			for (int i = 0; i < pyramid.tileSide; i++)
				for (int j = 0; j < pyramid.tileSide; j++) {
					draw(i, j, dx, dy, scale, canvas, paint);
				}
		} else if (allNot) {
		} else {
			for (int xy : targetPos) {

				// relative to tile left-top
				int x = xy / pyramid.tileSide;
				int y = xy % pyramid.tileSide;

				draw(x, y, dx, dy, scale, canvas, paint);
			}
		}
	}

	private void draw(int x, int y, float dx, float dy, int scale, BufferedImage canvas, Color paint) {

		x = Math.round((x + dx) / scale);
		y = Math.round((y + dy) / scale);

		if (x < 0 || y < 0 || x >= canvas.getWidth() || y >= canvas.getHeight()) {
			log.info("out of bound:" + x + "," + y);
		} else {
			canvas.setRGB(x, y, paint.getRGB());
		}
	}
}
