package azura.banshee.zebra2.zimage2.large2;

import java.awt.image.BufferedImage;

import azura.banshee.zebra2.tif2.ImageReader2;
import azura.banshee.zebra2.zatlas2.Zatlas2;
import azura.banshee.zebra2.zimage2.ZimageLarge2;
import common.algorithm.FoldIndex;
import common.collections.RectanglePlus;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;
import common.graphics.ImageUtil;

public class ZimageLarge2Old implements ZintCodecI {

	/**
	 * relative to the image center, estimated
	 */
	RectanglePlus boundingBox = new RectanglePlus();
	ZimageTiny2 tiny = new ZimageTiny2();
	ZimageLarge2 pyramid = new ZimageLarge2();

	public void load(Zatlas2 atlas, ImageReader2 reader) {

		boundingBox = reader.bbc;

		pyramid.load(atlas, reader);

		BufferedImage root = reader.getTile(FoldIndex.create(0, 0, 0).fi)
				.getImage();

		BufferedImage sourceTiny = ImageUtil.scale(root, 0.5);

		tiny.load(atlas, sourceTiny);
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		boundingBox.x = reader.readZint();
		boundingBox.y = reader.readZint();
		boundingBox.width = reader.readZint();
		boundingBox.height = reader.readZint();
		tiny.readFrom(reader);
		pyramid.readFrom(reader);
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(boundingBox.x);
		writer.writeZint(boundingBox.y);
		writer.writeZint(boundingBox.width);
		writer.writeZint(boundingBox.height);
		tiny.writeTo(writer);
		pyramid.writeTo(writer);
	}

}
