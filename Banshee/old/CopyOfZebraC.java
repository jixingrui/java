package azura.banshee.main;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import azura.banshee.chessboard.mask.MaskChecker;
import azura.banshee.chessboard.mask.MaskStripper;
import azura.banshee.chessboard.mask.Zmask;
import azura.banshee.chessboard.tif.ImageReader;
import azura.banshee.pano.PanoWriterJpg;
import azura.banshee.util.Konggui;
import azura.banshee.zbase.Zbase;
import azura.banshee.zbase.Zway;
import azura.banshee.zforest.Zforest;
import azura.gallerid.GalFile;
import azura.gallerid.GalPack;
import azura.gallerid.Swap;
import common.algorithm.FastMath;
import common.collections.buffer.ZintBuffer;
import common.util.FileUtil;

public class CopyOfZebraC {

	public static Logger log = Logger.getLogger(CopyOfZebraC.class);

	public static void main(String[] args) throws IOException {

//		 args = new String[] { "zebra", "Z:/temp/zebra/car.png" };
//		 args = new String[] { "zebra", "Z:/working/zebra/man.png" };
//		 args = new String[] { "zebra", "Z:/temp/废弃小镇测试有问题/废弃小镇.tif" };
		// args = new String[] { "zbase", "Z:/temp/zebra/room_a.base.png" };
		// args = new String[] { "mask_check",
		// "Z:/temp/zebra/townV2/town.mask.tif" };
//		 args = new String[] { "zmask", "Z:/temp/1左上角/右下扩边不好使/town.tif" };
		// args = new String[] { "zbase_planted", "Z:/temp/zebra/town.2.zforest"
		// };
		// args = new String[] { "zway", "Z:/temp/zebra/room_a.1.zbase_plus.png"
		// };

		if (args.length < 2)
			fail("java -jar Zebra.jar [zebra,mask_check,mask_strip,zmask,zbase,zbase_planted,zway,zpano,konggui] path");

		PropertyConfigurator.configure("log4j.properties");
		Logger.getLogger("io.netty").setLevel(Level.WARN);
		log.info("start");

		long start = System.currentTimeMillis();

		String mode = args[0];
		File input = new File(args[1]);
		if (!input.exists())
			fail("file not found");

		Swap.setLocation(input);

		switch (mode) {
		case "zebra": {
			String output = input.getParent() + "/"
					+ FileUtil.getNoExt(input.getName()) + ".zebra";

			GalPack gp = new GalPack();
			byte[] raw = null;

			Zebra zebra = new Zebra();

			zebra.load(input);

			raw = zebra.toBytes();
			zebra.extractMe5To(gp);

			byte[] zip = FileUtil.compress(raw);
			gp.setMaster(zip);
			gp.writeTo(output);
			gp.cleanUp();
		}
			break;
		case "mask_check":
			MaskChecker.check(input);
			break;
		case "mask_strip":
			MaskStripper.strip(input);
			break;
		case "zmask": {
			String output = input.getParent() + "/"
					+ FileUtil.getNoExt(input.getName()) + ".zmask";

			Zmask zmask = new Zmask();
			zmask.load(input);

			byte[] zip = FileUtil.compress(zmask.toBytes());

			GalPack gp = new GalPack();
			gp.setMaster(zip);
			zmask.extractMe5To(gp);
			gp.writeTo(output);
			gp.cleanUp();
		}
			break;
		case "zbase": {
			String fileName = input.getName();
			fileName = FileUtil.getNoExt(fileName, true);
			String output = input.getParent() + "/" + fileName + ".zbase";

			ImageReader pt = ImageReader.load(input.getPath(), true);
			Zbase zbase = new Zbase();
			zbase.load(pt, Color.black, true);
			pt.dispose();

			byte[] raw = zbase.toBytes();
			byte[] zip = FileUtil.compress(raw);

			GalPack gp = new GalPack();
			gp.setMaster(zip);
			gp.writeTo(output);
			gp.cleanUp();
		}
			break;
		case "zbase_planted": {
			String output = input.getParent() + "/"
					+ FileUtil.getNoExt(input.getName()) + ".zbase.planted.png";
			GalPack gp = GalPack.read(input);
			byte[] data = gp.getMaster();
			data = FileUtil.uncompress(data);
			Zforest zf = new Zforest();
			zf.fromBytes(data);
			zf.draw(output);
			gp.cleanUp();
		}
			break;

		case "zway": {
			String output = input.getParent() + "/"
					+ FileUtil.getNoExt(input.getName(), true) + ".zway";
			String debug = output + ".debug.png";

			Zway zway = new Zway();
			zway.load(input.getPath());

			GalPack gpOut = new GalPack();
			byte[] zip = FileUtil.compress(zway.toBytes());
			gpOut.setMaster(zip);
			gpOut.writeTo(output);
			gpOut.cleanUp();

			zway.draw(debug);
		}
			break;
		case "zpano": {
			String output = input.getParent() + "/" + input.getName()
					+ ".zpano";
			if (new File(output).exists()) {
				fail("file exists. overwrite is forbidden.");
			}

			byte[][][] data = PanoWriterJpg.write18(input);
			ZintBuffer zb = new ZintBuffer();
			GalPack gp = new GalPack();
			for (int scale = 0; scale <= 2; scale++) {
				ZintBuffer level = new ZintBuffer();
				for (int dir = 0; dir < 6; dir++) {
					level.writeBytesZ(data[dir][scale]);
				}
				byte[] levelB = level.toBytes();
				ME5Old me5 = new ME5Old(levelB).isWindows(true).isAndroid(true)
						.isIos(true).isCpu(true);
				GalFile.write(me5.toString(), levelB);
				zb.writeUTFZ(me5.toString());
				gp.addSlave(me5.toString());
			}
			gp.setMaster(zb.toBytes());
			gp.writeTo(output);
			gp.cleanUp();
		}
			break;
		case "konggui":
			Konggui.toMaxtrix(input.toString());
			break;
		default:
			break;
		}

		String time = FastMath.timeSpent(start);

		log.info("processing time=" + time + " by " + args[1]);
		// System.in.read();
		System.exit(0);
	}

	private static void fail(String msg) throws IOException {
		System.out.println(msg);
		// System.in.read();
		System.exit(0);
	}

}
// case "zforest": {
// String output = input.getParent() + "/"
// + FileUtil.getNoExt(input.getName()) + ".zway.png";
// GalPack gp = GalPack.read(input);
// byte[] data = gp.getMaster();
// data = FileUtil.uncompress(data);
// Zforest zf = new Zforest();
// zf.fromBytes(data);
// zf.draw(output);
// gp.cleanUp();
// }
// break;
// case "ztree": {
// String output = input.getParent() + "/"
// + FileUtil.getNoExt(input.getName()) + ".ztree";
//
// String ext = FileUtil.getExt(input.getName());
// String baseFileName = FileUtil.getNoExt(input.getPath()) + ".base."
// + ext;
//
// Zitem ztree = new Zitem();
// ztree.load(input.getPath(), baseFileName);
//
// byte[] raw = ztree.toBytes();
// byte[] zip = FileUtil.compress(raw);
//
// GalPack gp = new GalPack();
// gp.setMaster(zip);
// ztree.extractMc5(gp);
// gp.write(output);
// gp.cleanUp();
// }
// break;