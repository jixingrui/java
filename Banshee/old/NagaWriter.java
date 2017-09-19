package azura.banshee.nagaOld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.collections.buffer.ZintBuffer;

public class NagaWriter {

	public AtfAtlas atlas;
	private File[][] matrix;

	public NagaWriter(File[][] matrix) throws IOException {
		this.matrix = matrix;
		List<File> feed = new ArrayList<>();
		for (File[] row : matrix) {
			for (File f : row) {
				feed.add(f);
			}
		}
		atlas = new AtfAtlas(feed.toArray(new File[0]), 512, false);
	}

	public byte[] encode() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(matrix.length);
		for (int i = 0; i < matrix.length; i++) {
			zb.writeZint(matrix[i].length);
		}
		zb.writeBytes(atlas.encode());
		return zb.toBytes();
	}

	public static void main(String[] args) throws IOException {
//		File sourceFolder = new File("./input/atlas/");

//		File[][] matrix = FileMatrix.assemble(sourceFolder);
//
//		NagaWriter naga = new NagaWriter(matrix);
//		byte[] nagaZip = FileUtil.compress(naga.encode());
//
//		Gal_Pack gp = new Gal_Pack();
//		gp.setMaster(nagaZip);
//		naga.atlas.extractMd5(gp);
//		gp.encode("./input/atlas.naga");
	}

}
