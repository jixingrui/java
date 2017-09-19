package azura.banshee.nagaOld;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import azura.gallerid.GalPack;

import common.collections.buffer.ZintBuffer;

public class AtfAtlas {
	public List<Sheet> sheetList = new ArrayList<Sheet>();
	List<Frame> frameList = new ArrayList<Frame>();
	private int bound;

	public AtfAtlas(File[] list,int bound, boolean sizeSort) throws IOException {
		this.bound=bound;
		if(list.length==0)
			return;

		for (File file : list) {
			BufferedImage png = ImageIO.read(file);

			Frame f = new Frame(this,png);
			frameList.add(f);

			if (f.shrinkedImage.getWidth() > bound
					|| f.shrinkedImage.getHeight() > bound) {
				throw new IOException("image size larger than bound");
			}
		}

		Sheet s = new Sheet(bound);
		if (sizeSort) {
			TreeSet<Frame> fs = new TreeSet<Frame>(frameList);
			for (Frame f : fs)
				s = insert(s, f);
		} else {
			for (Frame f : frameList)
				s = insert(s, f);
		}
		s.seal();
		sheetList.add(s);
	}

	private Sheet insert(Sheet s, Frame f) {
		if (s.append(f)) {
			f.onSheet = sheetList.size();
			return s;
		} else {
			s.seal();
			sheetList.add(s);
			s = new Sheet(bound);
			return insert(s, f);
		}
	}

	public Frame getFrame(int i) {
		return frameList.get(i);
	}

	public byte[] encode() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(frameList.size());
		for (Frame f : frameList) {
			zb.writeBytes(f.encode());
		}
		return zb.toBytes();
	}

	public void extractMd5(GalPack gp) {
		for (Sheet s : sheetList) {
			gp.addSlave(s.md5[0]);
			gp.addSlave(s.md5[1]);
			gp.addSlave(s.md5[2]);
		}
	}

}
