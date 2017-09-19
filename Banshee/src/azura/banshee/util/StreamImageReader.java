package azura.banshee.util;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;

import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import com.sun.media.jai.codec.FileSeekableStream;

public class StreamImageReader {

	private FileSeekableStream fss = null;
	public PlanarImage image = null;

	public static StreamImageReader load(String fileName) {
		return new StreamImageReader(fileName);
	}

	public StreamImageReader(String fileName) {
		try {
			fss = new FileSeekableStream(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		RenderedImage im = JAI.create("stream", fss);
		image = PlanarImage.wrapRenderedImage(im);
	}

	public void dispose() {
		try {
			image = null;
			fss.close();
			fss = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static PlanarImage scale(PlanarImage source, float scale,
			boolean smooth) {
		ParameterBlock param = new ParameterBlock();
		param.addSource(source);
		param.add(scale);
		param.add(scale);
		param.add(0.0f);
		param.add(0.0f);
		if (smooth)
			param.add(new InterpolationBilinear());
		else
			param.add(new InterpolationNearest());

		return JAI.create("scale", param);
	}
}
