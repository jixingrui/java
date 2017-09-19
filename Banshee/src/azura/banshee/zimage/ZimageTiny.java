package azura.banshee.zimage;

import java.awt.image.BufferedImage;
import java.io.IOException;

import azura.banshee.zatlas.Zatlas;
import azura.banshee.zatlas.Zframe;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;

import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;

public class ZimageTiny implements BytesI, GalPackI {

	private Zatlas atlas = new Zatlas();

	public void load(BufferedImage source) throws IOException {

		Zframe frame = new Zframe().load(source, true);
		while (frame.blank_key_delta!=0) {
			atlas.add(frame);
			Zframe half = frame.getHalf();
			half.compact = ImageUtil.scale(frame.compact, 0.5);
			frame=half;
		}
		atlas.seal();
	}

	@Override
	public void extractMe5To(GalPack gp) {
		atlas.extractMe5To(gp);
	}

	@Override
	public void fromBytes(byte[] bytes) {
		atlas.fromBytes(bytes);
	}

	@Override
	public byte[] toBytes() {
		return atlas.toBytes();
	}

}
