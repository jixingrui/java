package old;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import azura.banshee.chessboard.dish.PlateType;
import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.fi.TileFi;
import azura.banshee.chessboard.mask.Shard;
import azura.banshee.naga.AtfAtlas;
import azura.banshee.util.AtfUtil;
import azura.banshee.util.FileList;
import azura.gallerid.Gal_File;
import azura.gallerid.Gal_Pack;

import common.algorithm.FoldIndex;
import common.collections.buffer.ZintBuffer;

public class TileDishEmbed extends TileFi<TileDishEmbed> {

	private static byte[] emptyPlate;

	private PlateType plateType;
	public String md5Plate, md5Cookie;
	private AtfAtlas aa;

	private List<Shard> shardList;

	public TileDishEmbed(int fi, PyramidFi<TileDishEmbed> pyramid) {
		super(fi, pyramid);
	}

	public void process(BufferedImage land256, List<Shard> shardList)
			throws IOException {
		this.shardList=shardList;

		// plate
		plateType = PlateType.check(land256);
		byte[] plate;
		if (plateType != PlateType.Empty)
			plate = AtfUtil.image2atf(land256);
		else {
			if (emptyPlate == null)
				emptyPlate = AtfUtil.image2atf(land256);
			plate = emptyPlate;
		}

		md5Plate = Gal_File.putData(plate);

		// cookie atlas
		FileList fl = new FileList();
		for (int i = 0; i < shardList.size(); i++) {
			Shard shard = shardList.get(i);
			BufferedImage shardCanvas = new BufferedImage(shard.width,
					shard.height, BufferedImage.TYPE_INT_ARGB);
			for (int xyOnShard : shard.mask) {
				int xOnShard = xyOnShard % shard.width;
				int yOnShard = xyOnShard / shard.width;
				int color = land256.getRGB((shard.x + xOnShard) % 256,
						(shard.y + yOnShard) % 256);
				shardCanvas.setRGB(xOnShard, yOnShard, color);
			}
			fl.append(shardCanvas);
		}
		aa = new AtfAtlas(fl.getList(), 256, true);

		// cookie data
//		int sl2 = FoldIndex.getBound(super.getLevel()) * 256 / 2;
//		ZintBuffer zb = new ZintBuffer();
//		zb.writeZint(shardList.size());
//		for (Shard shard : shardList) {
//			shard.x -= sl2;
//			shard.y -= sl2;
//			shard.yFoot -= sl2;
//			zb.writeBytes(shard.toBytes());
//		}
//		zb.writeBytes(aa.encode());
//		byte[] cookieData = FileUtil.compress(zb.toBytes());
//		md5Cookie = Gal_File.putData(cookieData);

		// debug
		// BufferedImage bi = new BufferedImage(256, 256,
		// BufferedImage.TYPE_INT_ARGB);
		// for (MaskShard shard : shardList) {
		// for (int pos : shard.mask) {
		// int x = pos / shard.width;
		// int y = pos % shard.width;
		// bi.setRGB(x, y, 0xff0000ff);
		// }
		// }
		//
		// ImageIO.write(bi, "png", new File("./input/" + fi + ".png"));
	}

	public byte[] encode() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(plateType.ordinal());
		zb.writeUTF(md5Plate);
//		zb.writeUTF(md5Cookie);
		
		int sl2 = FoldIndex.getBound(super.getLevel()) * 256 / 2;
		zb.writeZint(shardList.size());
		for (Shard shard : shardList) {
			shard.x -= sl2;
			shard.y -= sl2;
			shard.yFoot -= sl2;
			zb.writeBytes(shard.toBytes());
		}
		zb.writeBytes(aa.encode());
		
//		zb.writeBytes(Gal_File.getData(md5Cookie));
		return zb.toBytes();
	}

	public void extractMd5(Gal_Pack gp) {
		gp.addSlave(md5Plate);
//		gp.addSlave(md5Cookie);
		aa.extractMd5(gp);
	}
}
