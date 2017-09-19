package azura.gallerid;

import java.io.File;

import common.algorithm.crypto.Rot;
import common.util.FileUtil;

public class GalFile {

	private static File swapFolder;

	private static File getSwapFolder() {
		if (swapFolder == null) {
			swapFolder = Swap.applyNewSwapSubFolder();
		}
		return swapFolder;
	}

	public synchronized static void write(String mc5, byte[] data) {
		Rot.encrypt(data);
		FileUtil.write(getPath(mc5), data);
	}

	public static void deleteData(String mc5) {
		new File(getPath(mc5)).delete();
	}

	public synchronized static byte[] read(String mc5) {
		if (mc5.length() == 0)
			throw new Error();

		byte[] data = FileUtil.read(getPath(mc5));
		Rot.decrypt(data);
		return data;
	}

	private static String getPath(String mc5) {
		return getSwapFolder() + "/" + mc5;
	}

//	public static void main(String[] args) throws IOException {
//		byte[] b = new byte[100];
//		for (int i = 0; i < 100; i++) {
//			b[i] = (byte) i;
//		}
//		// putData(b);
//	}

}
