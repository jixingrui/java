package azura.banshee.chessboard.dish;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.fi.TileFi;
import azura.banshee.chessboard.land.TextureType;
import azura.banshee.chessboard.mask.Shard;
import azura.banshee.naga.AtfAtlas;
import azura.banshee.util.AtfUtil;
import azura.banshee.util.FrameListOld;
import azura.gallerid.GalFile;
import azura.gallerid.GalPack;

import common.collections.buffer.ZintBuffer;
import common.util.FileUtil;

public class TileDishOld extends TileFi<TileDishOld> {

	private static byte[][] emptyPlate;

	private TextureType plateType;
	private AtfAtlas aa;
	public String md5Cookie;
	public String[] md5Plate = new String[3];

	public TileDishOld(int fi, PyramidFi<TileDishOld> pyramid) {
		super(fi, pyramid);
	}

	public void process(BufferedImage land256, List<Shard> shardList)
			throws IOException {

		// plate
		plateType = TextureType.check(land256);
		byte[][] plate;
		if (plateType != TextureType.Empty)
			plate = AtfUtil.image2atf(land256);
		else {
			if (emptyPlate == null)
				emptyPlate = AtfUtil.image2atf(land256);
			plate = emptyPlate;
		}

		md5Plate[0] = GalFile.putData(plate[0]);
		md5Plate[1] = GalFile.putData(plate[1]);
		md5Plate[2] = GalFile.putData(plate[2]);

		// cookie atlas
		FrameListOld fl = new FrameListOld();
		for (int i = 0; i < shardList.size(); i++) {
			Shard shard = shardList.get(i);
			BufferedImage shardImage = new BufferedImage(shard.width,
					shard.height, BufferedImage.TYPE_INT_ARGB);
			for (int xyOnShard : shard.mask) {
				int xOnShard = xyOnShard % shard.width;
				int yOnShard = xyOnShard / shard.width;
				int color = land256.getRGB((shard.x + xOnShard) % 256,
						(shard.y + yOnShard) % 256);
				shardImage.setRGB(xOnShard, yOnShard, color);
			}
			fl.append(shardImage);
		}
		aa = new AtfAtlas(fl.getList(), 256, true);

		// cookie data
//		int sl2 = FoldIndexOld.getBound(z) * 256 / 2;
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(shardList.size());
		for (Shard shard : shardList) {
			zb.writeBytes(shard.toBytes());
		}
		zb.writeBytes(aa.encode());
		byte[] cookieData = FileUtil.compress(zb.toBytes());
		md5Cookie = GalFile.putData(cookieData);
	}

	public byte[] encode() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(plateType.ordinal());
		zb.writeUTF(md5Plate[0]);
		zb.writeUTF(md5Plate[1]);
		zb.writeUTF(md5Plate[2]);
		zb.writeUTF(md5Cookie);
		return zb.toBytes();
	}

	public void extractMd5(GalPack gp) {
		gp.addSlave(md5Plate[0]);
		gp.addSlave(md5Plate[1]);
		gp.addSlave(md5Plate[2]);
		gp.addSlave(md5Cookie);
		aa.extractMd5(gp);
	}
}
