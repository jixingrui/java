package azura.banshee.zforest.old;

import azura.banshee.zforest.Zitem;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Zplanted implements BytesI, GalPackI {
	public Zitem ztree = new Zitem();
	public String name;

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		name = zb.readUTFZ();
		ztree.fromBytes(zb.readBytes());
	}

	@Override
	public byte[] toBytes() {
		return null;
	}

	@Override
	public void extractMe5To(GalPack gp) {
		ztree.extractMe5To(gp);
	}

}
