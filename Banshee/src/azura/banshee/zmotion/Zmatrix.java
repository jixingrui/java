package azura.banshee.zmotion;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import azura.banshee.main.ZebraBranchI;
import azura.banshee.util.FileMatrix;
import azura.gallerid.GalPack;
import common.collections.buffer.ZintBuffer;

public class Zmatrix implements ZebraBranchI {
	// public static final int ZHLINE = 0;
	// public static final int ZVLINE = 1;
	// public static final int ZMATRIX = 2;

	// public int type;
	public volatile int cols, rows;
	public List<Zline> lineList = new ArrayList<>();

	// private volatile int width, height;

	public void load(File source) throws IOException {

		FileMatrix fm = FileMatrix.assemble(source);
		cols = fm.cols;
		rows = fm.rows;

		if (rows > 1 && cols > 1) {
			// type = ZMATRIX;
			loadMatrix(fm);
		} else if (cols > 1) {
			// type = ZHLINE;
			loadHline(fm);
		} else if (rows > 1) {
			// type = ZVLINE;
			loadVline(fm);
		} else
			throw new IllegalArgumentException(
					"Zmotion: folder does not contain a matrix");
	}

	private void loadVline(FileMatrix fm) throws IOException {
		File[] source = new File[fm.rows];
		for (int i = 0; i < fm.rows; i++) {
			source[i] = fm.matrix[i][0];
		}
		Zline vline = new Zline(source);
		vline.load();
		lineList.add(vline);

		// width = Math.max(width, vline.width);
		// height = Math.max(height, vline.height);
	}

	private void loadHline(FileMatrix fm) throws IOException {
		Zline hline = new Zline(fm.matrix[0]);
		hline.load();
		lineList.add(hline);

		// width = Math.max(width, hline.width);
		// height = Math.max(height, hline.height);
	}

	private void loadMatrix(FileMatrix fm) throws IOException {

		for (int i = 0; i < fm.rows; i++) {
			Zline za = new Zline(fm.matrix[i]);
			lineList.add(za);
		}

		lineList.parallelStream().forEach(za -> {
			try {
				za.load();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// width = Math.max(width, za.width);
			// height = Math.max(height, za.height);
			});
	}

	@Override
	public void extractMe5To(GalPack gp) {
		for (Zline za : lineList)
			za.extractMe5To(gp);
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb=new ZintBuffer(data);
		cols=zb.readZint();
		rows=zb.readZint();
		int size=zb.readZint();
		for(int i=0;i<size;i++){
			Zline zline=new Zline();
			lineList.add(zline);
			zline.fromBytes(zb.readBytesZ());
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(cols);
		zb.writeZint(rows);
		// zb.writeZint(width);
		// zb.writeZint(height);
		zb.writeZint(lineList.size());
		for (int i = 0; i < lineList.size(); i++) {
			zb.writeBytesZ(lineList.get(i).toBytes());
		}
		return zb.toBytes();
	}

	@Override
	public Rectangle getBoundingBox() {
		return lineList.get(0).boundingBox;
	}

	// @Override
	// public int getWidth() {
	// return width;
	// }
	//
	// @Override
	// public int getHeight() {
	// return height;
	// }
}
