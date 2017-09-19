package azura.banshee.chessboard.mask;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.fi.TileFi;
import azura.banshee.chessboard.tif.ImageReader;
import azura.banshee.zebra2.zatlas2.ZGroup2;
import azura.banshee.zebra2.zatlas2.Zatlas2;
import azura.banshee.zebra2.zatlas2.Zframe2;
import azura.gallerid.GalPackI5;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class ZmaskTile2 extends TileFi<ZmaskTile2> implements BytesI, GalPackI5 {

	static Logger log = Logger.getLogger(ZmaskTile2.class);

	public Zatlas2 atlas;
	public List<Shard> shardList = new ArrayList<Shard>();

	public ZmaskTile2(int fi, PyramidFi<ZmaskTile2> pyramid) {
		super(fi, pyramid);
		atlas = new Zatlas2(1024);
	}

	public void putShard(Shard shard) {
		shardList.add(shard);
	}

	public void processTile(ImageReader land) {

		ZGroup2 zg = atlas.newGroup();

		Iterator<Shard> it = shardList.iterator();
		while (it.hasNext()) {
			Shard shard = it.next();
			BufferedImage shardImage = new BufferedImage(shard.width, shard.height, BufferedImage.TYPE_INT_ARGB);
			boolean empty = true;
			for (int xyOnShard : shard.lbs) {
				int xOnShard = xyOnShard % shard.width;
				int yOnShard = xyOnShard / shard.width;
				int xOnMask = shard.x + xOnShard;
				int yOnMask = shard.y + yOnShard;
				int xOnLand = xOnMask;
				int yOnLand = yOnMask;
				int color = land.getRGB(xOnLand, yOnLand, z + land.zMax - pyramid.zMax);
				if ((color >> 24) != 0) {
					empty = false;
					shardImage.setRGB(xOnShard, yOnShard, color);
				}
			}
			if (empty) {
				log.warn("shard empty: " + this.toString());
				it.remove();
				continue;
			}

			Zframe2 frame = zg.newFrame();
			frame = frame.load(shardImage);
			if (frame != null) {
				frame.anchorY += shard.yFoot - shard.y;
				// atlas.add(frame);
			} else {
				log.error("shard empty filter failed");
				it.remove();
			}
		}
		zg.seal();
		atlas.seal();
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytesZ(atlas.toBytes());
		zb.writeZint(shardList.size());
		for (Shard shard : shardList) {
			zb.writeZint(shard.x);
			zb.writeZint(shard.yFoot);
		}
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] data) {
	}

	@Override
	public void extractMc5(Set<String> slaveSet) {
		atlas.extractMc5(slaveSet);
	}

	public void draw(BufferedImage out) {
		for (Shard shard : shardList) {
			for (int xyOnShard : shard.lbs) {
				int xOnShard = xyOnShard % shard.width;
				int yOnShard = xyOnShard / shard.width;
				int xOut = xOnShard + shard.x + out.getWidth() / 2;
				int yOut = yOnShard + shard.y + out.getHeight() / 2;
				out.setRGB(xOut, yOut, shard.color);
			}
		}
	}

	public void cleanUp() {
		atlas.cleanUp();
	}

}
