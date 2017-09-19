package azura.banshee.chessboard.mask;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.fi.TileFi;
import azura.banshee.chessboard.tif.ImageReader;
import azura.banshee.zatlas.Zatlas;
import azura.banshee.zatlas.Zframe;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class ZmaskTile extends TileFi<ZmaskTile> implements BytesI, GalPackI {

	static Logger log = Logger.getLogger(ZmaskTile.class);

	public Zatlas atlas = new Zatlas();
	public List<Shard> shardList = new ArrayList<Shard>();

	public ZmaskTile(int fi, PyramidFi<ZmaskTile> pyramid) {
		super(fi, pyramid);
	}

	public void putShard(Shard shard) {
		shardList.add(shard);
	}

	public void processTile(ImageReader land) {
		
		Iterator<Shard> it = shardList.iterator();
		while (it.hasNext()) {
			Shard shard = it.next();
			BufferedImage shardImage = new BufferedImage(shard.width,
					shard.height, BufferedImage.TYPE_INT_ARGB);
			boolean empty = true;
			for (int xyOnShard : shard.lbs) {
				int xOnShard = xyOnShard % shard.width;
				int yOnShard = xyOnShard / shard.width;
				int xOnMask = shard.x + xOnShard;
				int yOnMask = shard.y + yOnShard;
				int xOnLand=xOnMask;
				int yOnLand=yOnMask;
//				if(pyramid.zMax<land.zMax){
//					xOnLand-=FoldIndex.sideLength(pyramid.zMax)*pyramid.tileSide/2;
//					yOnLand-=FoldIndex.sideLength(pyramid.zMax)*pyramid.tileSide/2;
//					xOnLand+=FoldIndex.sideLength(land.zMax)*pyramid.tileSide/2;
//					yOnLand+=FoldIndex.sideLength(land.zMax)*pyramid.tileSide/2;
//				}
				int color = land.getRGB(xOnLand, yOnLand, z+land.zMax-pyramid.zMax);
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

			Zframe frame = new Zframe();
			frame = frame.load(shardImage, false);
			if (frame != null) {
				frame.anchorY += shard.yFoot - shard.y;
				atlas.add(frame);
			} else {
				log.error("shard empty filter failed");
				it.remove();
			}
		}
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
	public void extractMe5To(GalPack gp) {
		atlas.extractMe5To(gp);
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

}
