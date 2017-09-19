package azura.banshee.zatlas;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import common.collections.RectanglePlus;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;

public class Zframe implements BytesI {

	static Logger log = Logger.getLogger(Zframe.class);

	// public boolean isBlank = true;
	public int blank_key_delta = 0;
	/**
	 * Let the logical position(point) be the origin. The anchor is the image
	 * center on the axis;
	 */
	public float anchorX = 0;
	public float anchorY = 0;
	public int idxSheet;
	public Rectangle rectOnSheet = new Rectangle();

	public BufferedImage compact;
	public int idx;

	/**
	 * @param center_corner
	 *            is the image "position" on the center or corner of the source
	 */
	public Zframe load(BufferedImage source, boolean center_corner) {

		RectanglePlus boundingBox = ImageUtil.getBoundingBox(source);
		if (boundingBox.width == 0 || boundingBox.height == 0) {
			log.info("empty shard");
			return null;
		}

		// isBlank = false;
		blank_key_delta = 1;

		compact = ImageUtil.getSubImage(source, boundingBox.x, boundingBox.y,
				boundingBox.width, boundingBox.height);

		if (center_corner) {
			anchorX = -boundingBox.x - (float) boundingBox.width / 2
					+ (float) source.getWidth() / 2;
			anchorY = -boundingBox.y - (float) boundingBox.height / 2
					+ (float) source.getHeight() / 2;
		} else {
			anchorX = -boundingBox.x - (float) boundingBox.width / 2;
			anchorY = -boundingBox.y - (float) boundingBox.height / 2;
		}

		rectOnSheet.width = boundingBox.width;
		rectOnSheet.height = boundingBox.height;

		return this;
	}

	public Zframe getHalf() {
		Zframe half = new Zframe();
		if (blank_key_delta == 0)
			return half;
		else if (rectOnSheet.width / 2 >= 16 && rectOnSheet.height / 2 >= 16) {
			half.anchorX = anchorX / 2;
			half.anchorY = anchorY / 2;
			half.rectOnSheet.width /= 2;
			half.rectOnSheet.height /= 2;
			// half.isBlank = false;
			half.blank_key_delta = 1;
			return half;
		} else {
			return half;
		}
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb = new ZintBuffer(data);
		blank_key_delta = zb.readZint();
		if (blank_key_delta != 0) {
			idxSheet = zb.readZint();
			rectOnSheet.x = zb.readZint();
			rectOnSheet.y = zb.readZint();
			rectOnSheet.width = zb.readZint();
			rectOnSheet.height = zb.readZint();
			anchorX = (float) zb.readDouble();
			anchorY = (float) zb.readDouble();
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		// zb.writeBoolean(isBlank);
		zb.writeZint(blank_key_delta);
		if (blank_key_delta != 0) {
			zb.writeZint(idxSheet);
			zb.writeZint(rectOnSheet.x);
			zb.writeZint(rectOnSheet.y);
			zb.writeZint(rectOnSheet.width);
			zb.writeZint(rectOnSheet.height);
			zb.writeDouble(anchorX);
			zb.writeDouble(anchorY);
		}
		return zb.toBytes();
	}

	public int getWidth() {
		if (blank_key_delta == 0)
			return 0;
		else
			return (int) (Math.max(Math.abs(anchorX + rectOnSheet.width / 2),
					Math.abs(anchorX + rectOnSheet.width / 2)) * 2);
	}

	public int getHeight() {
		if (blank_key_delta == 0)
			return 0;
		else
			return (int) (Math.max(Math.abs(anchorY + rectOnSheet.height / 2),
					Math.abs(anchorY + rectOnSheet.height / 2)) * 2);
	}

}
