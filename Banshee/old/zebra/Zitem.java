package azura.banshee.zforest;

import azura.banshee.main.Zebra;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;

import common.collections.bitset.lbs.LBSet;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Zitem implements BytesI, GalPackI {

	public String name;
	public int rootX;
	public int rootY;
	public Zebra zebra = new Zebra();
	public LBSet zbase = new LBSet();

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		name=zb.readUTFZ();
		rootX=zb.readZint();
		rootY=zb.readZint();
		zebra.fromBytes(zb.readBytesZ());
		zbase.fromBytes(zb.readBytesZ());
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeUTFZ(name);
		zb.writeZint(rootX);
		zb.writeZint(rootY);
		zb.writeBytesZ(zebra.toBytes());
		zb.writeBytesZ(zbase.toBytes());
		return zb.toBytes();
	}

//	public void load(String bg, String base) throws IOException {
//		ImageReader reader = ImageReader.load(bg,  true);
//
//		Zimage zimage = new Zimage();
//		zimage.load(reader);
//		zebra.branch = zimage;
//
//		ImageReader pt = ImageReader.load(base,  false);
//		pt.bgColor = Color.white;
//
//		zbase.load(pt, Color.BLACK, false);
//	}

	@Override
	public void extractMc5To(GalPack gp) {
		zebra.extractMc5To(gp);
	}

}
