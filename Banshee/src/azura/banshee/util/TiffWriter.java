package azura.banshee.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import com.sun.media.jai.codec.TIFFEncodeParam;

public class TiffWriter {

	public static void write(PlanarImage pi, String fileName, int tileSide) {
		TIFFEncodeParam param = new TIFFEncodeParam();
		param.setWriteTiled(true);
		param.setTileSize(tileSide, tileSide);
		param.setCompression(TIFFEncodeParam.COMPRESSION_DEFLATE);
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(fileName);
			JAI.create("encode", pi, fos, "TIFF", param);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				pi.dispose();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
