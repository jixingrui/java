package azura.banshee.zbase;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.BitSet;

import azura.banshee.chessboard.fi.TileFi;

import common.algorithm.FastMath;
import common.collections.bitset.lbs.LBSet;
import common.collections.buffer.BytesI;
import common.collections.buffer.ZintBuffer;

public class CopyOfZbaseTile extends TileFi<CopyOfZbaseTile> implements BytesI {

	private LBSet targetPos = new LBSet();
	private boolean allTarget = true;
	private boolean allNot = true;

	private BitSet bs;

	// private int tileSide;

	public CopyOfZbaseTile(int fi, Zbase pyramid) {
		super(fi, pyramid);
	}

	public void load(BufferedImage image, Color target, boolean reverse) {

		// tileSide = ((Zbase) pyramid).tileSide;
		for (int i = 0; i < pyramid.tileSide; i++)
			for (int j = 0; j < pyramid.tileSide; j++) {
				int color = image.getRGB(i, j);
				boolean isTarget = (color == target.getRGB()) ^ reverse;
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

			return bs.get(x * pyramid.tileSide + y);
		}
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb = new ZintBuffer(data);
		allTarget = zb.readBoolean();
		allNot = zb.readBoolean();
		targetPos.fromBytes(zb.readBytes());
		// pyramid.tileSide = ((Zbase) pyramid).pyramid.tileSide;
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBoolean(allTarget);
		zb.writeBoolean(allNot);
		zb.writeBytes(targetPos.toBytes());
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
		int scale = FastMath.pow2(dz);
		float dx = dxg + xp * pyramid.tileSide - FastMath.pow2(dz)
				* pyramid.tileSide / 2 + canvas.getWidth() * scale / 2;
		float dy = dyg + yp * pyramid.tileSide - FastMath.pow2(dz)
				* pyramid.tileSide / 2 + canvas.getHeight() * scale / 2;

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

	private void draw(int x, int y, float dx, float dy, int scale,
			BufferedImage canvas, Color paint) {

		x = Math.round((x + dx) / scale);
		y = Math.round((y + dy) / scale);

		if (x < 0 || y < 0 || x >= canvas.getWidth() || y >= canvas.getHeight()) {
//			log.info("draw out of bounds: " + x + "," + y);
			return;
		}
		canvas.setRGB(x, y, paint.getRGB());
	}
}

// relative to global left-top
// x += this.xp * tileSide;
// y += this.yp * tileSide;

// to global center
// x -= FastMath.pow2(this.z) * tileSide / 2;
// y -= FastMath.pow2(this.z) * tileSide / 2;

// move
// x += dxg;
// y += dyg;

// scale
// x = Math.round((float) x / scale + canvas.getWidth() / 2);
// y = Math.round((float) y / scale + canvas.getHeight() / 2);

// // relative to canvas left-top
// x += canvas.getWidth() / 2;
// y += canvas.getHeight() / 2;

// x += 1;