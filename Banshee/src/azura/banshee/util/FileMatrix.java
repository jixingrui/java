package azura.banshee.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class FileMatrix {
	public File[][] matrix;
	public int rows;
	public int cols;
	private HashMap<Integer, HashMap<Integer, File>> matrixTemp = new HashMap<Integer, HashMap<Integer, File>>();

	public static FileMatrix assemble(File source) throws IOException {
		FileMatrix fm = new FileMatrix(source);
		return fm;
	}

	private FileMatrix(File source) throws IOException {
		if (source.isFile()) {
			matrix = new File[][] { { source } };
		} else {
			for (File f : source.listFiles())
				push(f);
			seal();
		}
	}

	private File[][] seal() throws IOException {
		matrix = new File[rows][];
		for (int i = 0; i < rows; i++) {
			matrix[i] = new File[cols];
		}
		for (Entry<Integer, HashMap<Integer, File>> er : matrixTemp.entrySet()) {
			int rowIdx = er.getKey();
			HashMap<Integer, File> row = er.getValue();
			for (Entry<Integer, File> ef : row.entrySet()) {
				int frameIdx = ef.getKey();
				File frame = ef.getValue();
				matrix[rowIdx][frameIdx] = frame;
			}
		}
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) {
				if (matrix[i][j] == null) {
					throw new IOException("matrix is not full rectangle");
				}
			}
		return matrix;
	}

	private void push(File frame) throws IOException {

		String fileName = frame.getName();

		String[] chunk = fileName.split("[_\\.]");
		int rowIdx = Integer.parseInt(chunk[0]);
		int frameIdx = Integer.parseInt(chunk[1]);
		rows = Math.max(rows, rowIdx + 1);
		cols = Math.max(cols, frameIdx + 1);

		push(rowIdx, frameIdx, frame);
	}

	private void push(int rowIdx, int frameIdx, File frame) throws IOException {
		HashMap<Integer, File> row = getRow(rowIdx);
		File old = row.put(frameIdx, frame);
		if (old != null) {
			throw new IOException("duplicate frames");
		}
	}

	private HashMap<Integer, File> getRow(int rowIdx) {
		HashMap<Integer, File> row = matrixTemp.get(rowIdx);
		if (row == null) {
			row = new HashMap<Integer, File>();
			matrixTemp.put(rowIdx, row);
		}
		return row;
	}
}
