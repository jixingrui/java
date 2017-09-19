package azura.banshee.zebra2.zatlas2;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import azura.banshee.util.TextureType;
import azura.banshee.zebra2.RectC;
import azura.gallerid.GalFile;
import common.algorithm.MC5;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;

public class Zframe2 implements BytesI {

	static Logger log = Logger.getLogger(Zframe2.class);

	public int blank_key_delta = 0;
	/**
	 * Let the logical position(point) be the origin. The anchor is the image
	 * center on the axis;
	 */
	public float anchorX = 0;
	public float anchorY = 0;
	public int idxSheet;
	public Rectangle rectOnSheet = new Rectangle();

	// cache
	public MC5 mc5Original;
	private Rectangle boundingBox;
	public final int idxInAtlas;

	public TextureType textureType = TextureType.Init;

	private int sheetSize;

	Zframe2(int idxInAtlas,int sheetSize) {
		this.idxInAtlas = idxInAtlas;
		this.sheetSize=sheetSize;
	}

	public RectC getBoundingBox() {
		RectC result = new RectC();
		result.xc = anchorX;
		result.yc = anchorY;
		result.width = rectOnSheet.width;
		result.height = rectOnSheet.height;
		return result;
	}

	public Zframe2 load(BufferedImage source) {
		
//		if(source.getWidth()<4 || source.getHeight()<4)
//			throw new Error("frame image smaller than 4x4");

//		boundingBox = ImageUtil.getBoundingBox4x4(source);
		boundingBox = ImageUtil.getBoundingBox(source);

//		log.debug("frame bounding box = "+boundingBox.toString());

		if (boundingBox.width > sheetSize
				|| boundingBox.height > sheetSize)
			throw new Error("frame size must not exceed sheet limit "
					+ sheetSize);

		if (boundingBox.width == 0 || boundingBox.height == 0) {
//			log.debug("empty frame: " + boundingBox.width + ","
//					+ boundingBox.height);

			blank_key_delta = 0;
			return this;
		}

		blank_key_delta = 1;

		textureType = TextureType.check(source, boundingBox.x, boundingBox.y,
				boundingBox.width, boundingBox.height);

		anchorX = -boundingBox.x - (float) boundingBox.width / 2
				+ (float) source.getWidth() / 2;
		anchorY = -boundingBox.y - (float) boundingBox.height / 2
				+ (float) source.getHeight() / 2;

//		rectOnSheet.width = boundingBox.width;
//		rectOnSheet.height = boundingBox.height;

		// byte[] bitmap = null;
		// if (textureType == TextureType.Alpha)
		// bitmap = ImageUtil.encodePng(compact);
		// else if (textureType == TextureType.Solid)
		// bitmap = ImageUtil.encodeJpg(compact, 0.9f);
		// else
		// throw new Error();

		byte[] bitmap = ImageUtil.encodePng(source);

		mc5Original = new MC5(bitmap);
		GalFile.write(mc5Original.toString(), bitmap);

		return this;
	}

	public BufferedImage getOriginal() {
		byte[] bitmap = GalFile.read(mc5Original.toString());
		return ImageUtil.decodeBitmap(bitmap);
	}

	public BufferedImage getCompact() {

		BufferedImage source = getOriginal();

		BufferedImage compact;

		if (boundingBox.width == source.getWidth()
				&& boundingBox.height == source.getHeight()) {
			compact = source;
		} else {
			compact = ImageUtil.getSubImage(source, boundingBox.x,
					boundingBox.y, boundingBox.width, boundingBox.height);
		}

		return compact;
		// byte[] bitmap = GalFile.read(me5.toString());
		// return ImageUtil.decodeBitmap(bitmap);
	}

	public void loadHalf(Zframe2 source) {
		BufferedImage original = source.getOriginal();
		BufferedImage halfImage = ImageUtil.scale(original, 0.5);
		this.load(halfImage);
		// log.info("half:" + this.anchorX + "," + this.anchorY);
		// this.anchorX *= 2;
		// this.anchorY *= 2;
		// this.anchorX = source.anchorX / 2;
		// if(FastMath.isOdd(original.getWidth()))
		// this.anchorY = source.anchorY / 2;
		// if (FastMath.isOdd(original.getHeight())) {
		// log.info("is odd " + source.idxInAtlas);
		// this.anchorY += 0.5;
		// }
		// this.anchorX += source.anchorX;
		// this.anchorY += source.anchorY;
	}

	public boolean isSmall() {
		if (blank_key_delta == 0)
			return true;
		else if (getWidth() < 16 || getHeight() < 16)
			return true;
		else if (getWidth() * getHeight() < 2048)
			return true;
		else
			return false;
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
		return boundingBox.width;
	}

	public int getHeight() {
		return boundingBox.height;
	}

	public void cleanUp() {
		if (blank_key_delta > 0)
			GalFile.deleteData(mc5Original.toString());
	}

}
