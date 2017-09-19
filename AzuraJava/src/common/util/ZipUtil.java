package common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import common.logger.Trace;

public class ZipUtil {
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	ZipArchiveOutputStream zos = new ZipArchiveOutputStream(buffer);

	public ZipUtil() {
		zos.setEncoding("UTF-8");
	}

	public void appendFile(String fileName, byte[] content) {
		ZipArchiveEntry ze = new ZipArchiveEntry(fileName);
		try {
			zos.putArchiveEntry(ze);
			zos.write(content);
			zos.closeArchiveEntry();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void appendFile(String fileName, String content) {
		ZipArchiveEntry ze = new ZipArchiveEntry(fileName);
		try {
			zos.putArchiveEntry(ze);
			zos.write(content.getBytes("utf8"));
			zos.closeArchiveEntry();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] toBytes() {
		try {
			zos.flush();
			zos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	public static byte[] compress(byte[] raw) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
					byteArrayOutputStream);
			gzipOutputStream.write(raw);
			gzipOutputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.printf("Compression ratio %f\n",
				(1.0f * raw.length / byteArrayOutputStream.size()));
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] decompress(byte[] zip) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(zip)),
					out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toByteArray();
	}

	public static void writeFile(String path, String content) {
		File f = new File(path);
		File parent = f.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		// Trace.trace(f.getAbsolutePath());

		try {
			OutputStreamWriter osw = new OutputStreamWriter(
					new FileOutputStream(path), "utf-8");
			osw.write(content);
			osw.close();
		} catch (Exception e) {
			Trace.trace("gen code " + e);
		}
	}
}
