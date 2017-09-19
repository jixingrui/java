package common.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;

import com.google.common.io.Files;

public class FileUtil {

	static Logger log = Logger.getLogger(FileUtil.class);

	// public static File prepareSwapFolder() {
	// return prepareSwapFolder('c');
	// }

	public static String getExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
	}

	public static String getNoExt(String fileName) {
		return getNoExt(fileName, false);
	}

	public static String getNoExt(String fileName, boolean first_last) {
		if (fileName.contains(".")) {
			if (first_last)
				return fileName.substring(0, fileName.indexOf("."));
			else
				return fileName.substring(0, fileName.lastIndexOf("."));
		} else
			return fileName;
	}

	public static String getFolder(File f) throws IOException {
		return f.getCanonicalFile().getParent() + "/";
	}

	public static File getSideFolder(File f) throws IOException {
		String path = getNoExt(f.getAbsolutePath(), true);
		File folder = new File(path);
		return folder;
	}

	public static void delTree(File folder) {
		// File folder = new File(folderPath);
		if (folder == null || folder.isFile())
			return;

		folder.deleteOnExit();
		for (File oldFile : folder.listFiles()) {
			if (oldFile.isDirectory())
				delTree(oldFile);
			else
				oldFile.deleteOnExit();
		}
	}

	public static void prepareFolder(String path) {
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
			// Trace.trace(folder.getAbsolutePath());
		} else if (folder.isFile()) {
			// folder.delete();
		} else if (folder.isDirectory()) {
			// for (File oldFile : folder.listFiles()) {
			// oldFile.delete();
			// }
		}
	}

	public static String getSubFolderByFileName(File f) throws IOException {
		String folder = f.getCanonicalFile().getParent();
		String name = f.getName().substring(0, f.getName().indexOf("."));
		String result = folder + "/" + name + "/";
		File subFolder = new File(result);
		subFolder.mkdir();
		return result;
	}

	// public static String writeAsMd5(String folder, byte[] data, boolean
	// compress) {
	// if (compress) {
	// data = compress(data);
	// }
	// String md5 = MD5.b2s(data);
	// write(folder + "/" + md5, data);
	// return md5;
	// }

	public static boolean write(String filePath, byte[] data) {
		return write(filePath, data, true);
	}

	public static boolean write(String filePath, byte[] data, boolean overwrite) {
		FileOutputStream fos;
		try {
			if (overwrite == false && new File(filePath).exists()) {
				log.info("file already exists, skipping " + filePath);
				return true;
			}
			fos = new FileOutputStream(filePath, false);
			fos.write(data);
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			log.error(e);
			return false;
		} catch (IOException e) {
			log.error(e);
			return false;
		}
	}

	public static byte[] compress(byte[] raw) {
		return compress(raw, false);
	}

	public static byte[] compress(byte[] raw, boolean debug) {
		Deflater compressor = new Deflater();
		// compressor.setLevel(Deflater.BEST_COMPRESSION);
		compressor.setInput(raw);
		compressor.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(raw.length);
		// Compress the data
		byte[] buf = new byte[1024];
		while (!compressor.finished()) {
			int count = compressor.deflate(buf);
			bos.write(buf, 0, count);
		}
		try {
			bos.close();
		} catch (IOException e) {
		}
		// Get the compressed data
		byte[] compressedData = bos.toByteArray();
		if (debug) {
			String msg = "compression rate: " + (int) (100 - 100 * compressedData.length / raw.length) + "% ";
			msg += compressedData.length / 1000 + "k/" + raw.length / 1000 + "k";
			log.info(msg);
		}
		return compressedData;
	}

	public static byte[] uncompress(byte[] compressedData) {
		// Create the decompressor and give it the data to compress
		Inflater decompressor = new Inflater();
		decompressor.setInput(compressedData);

		// Create an expandable byte array to hold the decompressed data
		ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);

		// Decompress the data
		byte[] buf = new byte[1024];
		while (!decompressor.finished()) {
			try {
				int count = decompressor.inflate(buf);
				bos.write(buf, 0, count);
			} catch (DataFormatException e) {
			}
		}
		try {
			bos.close();
		} catch (IOException e) {
		}

		// Get the decompressed data
		byte[] decompressedData = bos.toByteArray();
		return decompressedData;
	}

	public static void append(String fileName, String string) {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(fileName, true));
			bw.write(string);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally { // always close the file
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ioe2) {
					// just ignore it
				}
			}
		}
	}

	public static byte[] read(File file) {
		try {
			return Files.toByteArray(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] read(String fileName) {
		return read(new File(fileName));
	}

	public static boolean delete(String fileName) {
		File target = new File(fileName);
		if (target.exists())
			return target.delete();
		else
			return false;
	}

	public static String getFileNameNoPathNoExt(String filePathName) {
		if (filePathName == null)
			return null;

		int dotPos = filePathName.lastIndexOf('.');
		int slashPos = filePathName.lastIndexOf('\\');
		if (slashPos == -1)
			slashPos = filePathName.lastIndexOf('\\');

		if (dotPos > slashPos) {
			return filePathName.substring(slashPos > 0 ? slashPos + 1 : 0, dotPos);
		}

		return filePathName.substring(slashPos > 0 ? slashPos + 1 : 0);
	}

	public static File getCreateFile(String path) {
		File file = new File(path);
		if (!file.exists())
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return file;
	}

}
