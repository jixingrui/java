package azura.banshee.zmotion;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import azura.banshee.zatlas.Zatlas;
import azura.banshee.zatlas.Zframe;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Zline implements BytesI, GalPackI {

	private int frames;
//	private LBSet keyFrames;
	private List<Zatlas> layers = new ArrayList<>();

	private File[] files;
	
	public Rectangle boundingBox;
	
	public Zline(){
		
	}

	public Zline(File[] files) {
		this.files = files;
	}

	public void load() throws IOException {
		this.frames = files.length;
		Zatlas bottom = new Zatlas();

		for (File file : files) {

			BufferedImage source = null;
			try {
				source = ImageIO.read(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Zframe zf = new Zframe().load(source, true);
			bottom.add(zf);
		}
		
//		BitSet bs=new BitSet();
//		for(int i=0;i<files.length;i++){
//			bs.set(i);
//		}
//		keyFrames=new LBSet(bs);

		bottom.seal();
		Zatlas current = bottom;
		while (current != null) {
			layers.add(current);
			current = current.getHalf();
		}
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb=new ZintBuffer(data);
		frames=zb.readZint();
		int size=zb.readZint();
		for(int i=0;i<size;i++){
			Zatlas atlas=new Zatlas();
			layers.add(atlas);
			atlas.fromBytes(zb.readBytesZ());
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(frames);
//		zb.writeBytes(keyFrames.toBytes());
		zb.writeZint(layers.size());
		for (int i = 0; i < layers.size(); i++) {
			zb.writeBytesZ(layers.get(i).toBytes());
		}
		return zb.toBytes();
	}

	@Override
	public void extractMe5To(GalPack gp) {
		for (Zatlas zl : layers) {
			zl.extractMe5To(gp);
		}
	}

}

