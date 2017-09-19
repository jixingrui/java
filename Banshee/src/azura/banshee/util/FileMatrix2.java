package azura.banshee.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class FileMatrix2 {
	public File[][] matrix;
	public int rows;
	public int cols;
	private HashMap<Integer, HashMap<Integer, File>> matrixTemp = new HashMap<Integer, HashMap<Integer, File>>();

	public static FileMatrix2 assemble(File input) {
		FileMatrix2 fm = new FileMatrix2();
		try {
			fm.load(input);
			return fm;
		} catch (IOException e) {
			return null;
		}
	}

	private FileMatrix2() {

	}

	private void load(File input) throws IOException {
		if (input.isFile()) {
			throw new IOException();
		} else {
			for (File f : input.listFiles())
				push(f);
			seal();
		}
	}

	private File[][] seal() throws IOException {
		if (rows * cols <= 1)
			throw new IOException();

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
		if (chunk.length != 3)
			throw new IOException();

		int rowIdx;
		int frameIdx;
		try {
			rowIdx = Integer.parseInt(chunk[0]);
			frameIdx = Integer.parseInt(chunk[1]);
		} catch (NumberFormatException e) {
			throw new IOException();
		}
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
