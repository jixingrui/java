package azura.banshee.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import azura.gallerid.Swap;

import common.algorithm.MD5;
import common.graphics.ImageUtil;
import common.util.FileUtil;

public class FrameListOld {

	private static File folder = Swap.newSwapFolder();
	private List<String> frameList = new ArrayList<String>();

	public void append(BufferedImage frame) {
		byte[] data = ImageUtil.encodePng(frame);
		String md5 = MD5.bytesToString(data);
		String cache = folder + "/" + md5;
		FileUtil.write(cache, data);
		new File(cache).deleteOnExit();

		frameList.add(cache);
	}

	public int size() {
		return frameList.size();
	}

	public File[] getList() {
		File[] result = new File[frameList.size()];
		for (int i = 0; i < frameList.size(); i++) {
			result[i] = new File(frameList.get(i));
		}
		return result;
	}

}
