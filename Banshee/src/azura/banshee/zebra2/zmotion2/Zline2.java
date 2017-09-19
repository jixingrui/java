package azura.banshee.zebra2.zmotion2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import azura.banshee.zebra2.RectC;
import azura.banshee.zebra2.zatlas2.ZGroup2;
import azura.banshee.zebra2.zatlas2.Zatlas2;
import azura.banshee.zebra2.zatlas2.Zframe2;

import common.collections.ArrayListAuto;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class Zline2 implements ZintCodecI {

	private int[][] zUp_frameList;
	private Zatlas2 atlas;
	private File[] files;

	public Zline2() {
	}

	public RectC getFirstBB() {
		Zframe2 frame = atlas.getFrame(zUp_frameList[0][0]);
		return frame.getBoundingBox();
	}

	public RectC getAverageBB() {
		RectC bb = new RectC();
		int length = zUp_frameList[0].length;
		for (int i = 0; i < length; i++) {
			Zframe2 current = atlas.getFrame(zUp_frameList[0][i]);
			bb.add(current.getBoundingBox());
		}
		bb.xc /= length;
		bb.yc /= length;
		bb.width /= length;
		bb.height /= length;
		return bb;
	}

	public void debug() {
		int rows = zUp_frameList.length;
		int frames = zUp_frameList[0].length;
		for (int r = 0; r < rows; r++) {
			System.out.println();
			for (int f = 0; f < frames; f++) {
				System.out.print(zUp_frameList[r][f] + ",");
			}
		}
		System.out.println("== end of Zline2 ==");
	}

	public int getLineLength() {
		if (zUp_frameList.length == 0)
			return 0;
		else
			return zUp_frameList[0].length;
	}

	public int getFrameIdxInAtlas(int zUp, int frameIdxInLine) {
		if (frameIdxInLine >= getLineLength())
			return -1;
		if (zUp_frameList.length <= zUp)
			return -1;

		return zUp_frameList[zUp][frameIdxInLine];
	}

	public void prepare(Zatlas2 atlas, File[] files) {
		this.atlas = atlas;
		this.files = files;
	}

	public void load(Zatlas2 atlas, File[] files) {
		prepare(atlas, files);
		load();
	}

	public void load() {
		ArrayListAuto<int[]> buffer = new ArrayListAuto<int[]>();

		// ========= the original layer ===========
		final Zframe2[] layerOriginal = new Zframe2[files.length];
		final ZGroup2 groupOriginal = atlas.newGroup();
		final int[] idxOriginal = new int[files.length];
		buffer.add(idxOriginal);
		ArrayList<Integer> iterator = new ArrayList<Integer>();
		for (int i = 0; i < files.length; i++) {
			iterator.add(i);
		}
		iterator.parallelStream().forEach(i -> {
			File file = files[i];
			BufferedImage source = null;
			try {
				source = ImageIO.read(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Zframe2 zf = groupOriginal.newFrame().load(source);
			layerOriginal[i] = zf;
			// group.add(zf);
				idxOriginal[i] = zf.idxInAtlas;
			});

		// =========== all the rest ============
		ZGroup2 groupRest = atlas.newGroup();
		Zframe2[] layerRest = groupRest.listToHalf(layerOriginal);
		int[] idxRest = idxOriginal;
		while (layerRest != null) {
			idxRest = new int[files.length];
			buffer.add(idxRest);
			for (int i = 0; i < layerRest.length; i++) {
				idxRest[i] = layerRest[i].idxInAtlas;
			}

			layerRest = groupRest.listToHalf(layerRest);
		}

		zUp_frameList = new int[buffer.size()][];
		for (int i = 0; i < buffer.size(); i++) {
			zUp_frameList[i] = buffer.get(i);
		}
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		int row = reader.readZint();
		int col = reader.readZint();
		zUp_frameList = new int[row][col];
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++) {
				zUp_frameList[i][j] = reader.readZint();
			}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(zUp_frameList.length);
		writer.writeZint(zUp_frameList[0].length);
		for (int i = 0; i < zUp_frameList.length; i++)
			for (int j = 0; j < zUp_frameList[0].length; j++) {
				writer.writeZint(zUp_frameList[i][j]);
			}

		// debug();
	}

}
