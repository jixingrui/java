package azura.banshee.zbase.station;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import azura.banshee.chessboard.fi.TileFi;
import common.algorithm.FastMath;
import common.collections.DupList;
import common.collections.bitset.lbs.LBSet;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class ZbaseTileR extends TileFi<ZbaseTileR> implements BytesI {

	private boolean allTrue = false;
	private boolean allFalse = true;
	private LBSet targetPos = new LBSet();
	private DupList dupList = new DupList();

	// cache
	private BitSet bs;
	private LinkedHashMap<Integer, Pixel> idx_Pixel = new LinkedHashMap<>();

	public HashMap<Integer, Integer> idx_xy;
	private byte[] dupListData;

	public ZbaseTileR(int fi, ZbaseR pyramid) {
		super(fi, pyramid);
	}

	public void load(BufferedImage image, Color target, boolean reverse) {

		int c = target.getRGB() & 0xffffff;
		for (int i = 0; i < pyramid.tileSide; i++)
			for (int j = 0; j < pyramid.tileSide; j++) {
				int color = image.getRGB(i, j) & 0xffffff;
				boolean isTarget = (color == c) ^ reverse;
				allTrue &= isTarget;
				allFalse &= !isTarget;
				targetPos.push(isTarget);

				// if (isTarget) {
				// int idx = xyToIdx(i, j);
				// idx_Pixel.put(idx, new Pixel(i, j));
				// }
			}
	}

	public boolean canWalk(int x, int y) {
		if (allTrue)
			return true;
		else if (allFalse)
			return false;
		else {
			if (bs == null)
				bs = targetPos.toBitSet();

			int idx = xyToIdx(x, y);
			return bs.get(idx);
		}
	}

	public Pixel getPixel(int x, int y) {
		int idx = xyToIdx(x, y);
		Pixel pix = idx_Pixel.get(idx);
		if (pix == null) {
			pix = new Pixel(x, y);
			idx_Pixel.put(idx, pix);
		}
		return pix;
	}

	private int xyToIdx(int x, int y) {
		return x * pyramid.tileSide + y;
	}

	@Override
	public byte[] toBytes() {
		if (idx_Pixel.isEmpty() == false) {
			Iterator<Integer> it = targetPos.iterator();
			while (it.hasNext()) {
				int idx = it.next();
				int xy = idx_Pixel.get(idx).nearestStation.xy;
				dupList.push(xy);
			}
		}

		ZintBuffer zb = new ZintBuffer();
		zb.writeBoolean(allTrue);
		zb.writeBoolean(allFalse);
		zb.writeBytesZ(targetPos.toBytes());
		// zb.writeZint(idx_Pixel.size());
		zb.writeBytesZ(dupList.toBytes());
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb = new ZintBuffer(data);
		allTrue = zb.readBoolean();
		allFalse = zb.readBoolean();
		targetPos.fromBytes(zb.readBytesZ());
		dupListData = zb.readBytesZ();
		// dupList.fromBytes(zb.readBytesZ());

		// Iterator<Integer> it = targetPos.iterator();
		// while (it.hasNext()) {
		// int idx = it.next();
		// int xy = dupList.get(idx);
		// idx_xy.put(idx, xy);
		// }
	}

	public int getStation(int x, int y) {
		ensureMatrix();
		int idx = xyToIdx(x, y);
		return idx_xy.get(idx);
		// return idx_Pixel.get(idx).nearestStation;
	}

	private void ensureMatrix() {
		if (idx_xy == null) {
			dupList.fromBytes(dupListData);
			idx_xy = new HashMap<>();
			Iterator<Integer> it=targetPos.iterator();
			int i=0;
			while(it.hasNext()){
				int idx=it.next();
				int xy = dupList.get(i);
				idx_xy.put(idx, xy);
				i++;
			}
//			targetPos.forEach(idx -> {
//				int xy = dupList.get(idx);
//				idx_xy.put(idx, xy);
//			});
		}
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
	public void draw(BufferedImage canvas, int dxg, int dyg, int dz) {
		int scale = FastMath.pow2x(dz);
		float dx = dxg + xp * pyramid.tileSide - FastMath.pow2x(z) * pyramid.tileSide / 2
				+ canvas.getWidth() * scale / 2;
		float dy = dyg + yp * pyramid.tileSide - FastMath.pow2x(z) * pyramid.tileSide / 2
				+ canvas.getHeight() * scale / 2;

		if (allTrue) {
			for (int i = 0; i < pyramid.tileSide; i++)
				for (int j = 0; j < pyramid.tileSide; j++) {
					draw(i, j, dx, dy, scale, canvas);
				}
		} else if (allFalse) {
		} else {
			for (int xy : targetPos) {

				// relative to tile left-top
				int x = xy / pyramid.tileSide;
				int y = xy % pyramid.tileSide;

				draw(x, y, dx, dy, scale, canvas);
			}
		}
	}

	private void draw(int x, int y, float dx, float dy, int scale, BufferedImage canvas) {

		int idx = xyToIdx(x, y);
		Pixel p = idx_Pixel.get(idx);
		if (p == null) {
			log.error("pixel not exist: " + x + "," + y);
			return;
		}
		if (p.nearestStation == null) {
			log.error("station not bound: " + x + "," + y);
			return;
		}

		x = Math.round((x + dx) / scale);
		y = Math.round((y + dy) / scale);

		if (x < 0 || y < 0 || x >= canvas.getWidth() || y >= canvas.getHeight()) {
			log.info("out of bound:" + x + "," + y);
		} else {
			canvas.setRGB(x, y, p.nearestStation.color);
		}
	}

}
