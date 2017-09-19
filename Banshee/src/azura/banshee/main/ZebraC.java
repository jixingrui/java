package azura.banshee.main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import azura.banshee.chessboard.mask.MaskChecker;
import azura.banshee.chessboard.mask.MaskStripper;
import azura.banshee.chessboard.mask.Zmask2;
import azura.banshee.pano.PanoWriterJpg;
import azura.banshee.util.Konggui;
import azura.banshee.zbase.Zbase;
import azura.banshee.zbase.station.RoadMap;
import azura.banshee.zbase.station.ZbaseR;
import azura.banshee.zbase.station.ZwayOld;
import azura.banshee.zebra2.Zebra2Cleaner;
import azura.banshee.zebra2.Zebra2Creator;
import azura.banshee.zebra2.tif2.ImageReader2;
import azura.banshee.zforest.Zforest;
import azura.gallerid.GalFile;
import azura.gallerid.GalPack;
import azura.gallerid.GalPack5;
import azura.gallerid.Swap;
import common.algorithm.FastMath;
import common.algorithm.MC5;
import common.collections.buffer.ZintBuffer;
import common.graphics.ImageUtil;
import common.util.FileUtil;

public class ZebraC {

	public static Logger log = Logger.getLogger(ZebraC.class);

	public static void main(String[] args) throws IOException {

		// args = new String[] { "zmask", "Z:/temp/测试图/废弃小镇v2_0.75/town.tif" };
		// args = new String[] { "map", "Z:/temp/测试图/废弃小镇v2_0.5/town.base.tif",
		// "3" };
		// args = new String[] { "zway", "Z:/temp/测试图/废弃小镇v2/sales.base.tif",
		// "2" };
		// args = new String[] { "map",
		// "Z:/temp/测试图/starcraft/starcraft.base.tif", "2" };
		// args = new String[] { "map",
		// "Z:/temp/测试图/starcraft/starcraft.base.tif", "2" };
		// args = new String[] { "zway", "Z:/temp/测试图/废弃小镇v2/town.base.tif", "3"
		// };
		// args = new String[] { "konggui", "Z:/temp/测试图/09" };
		// args = new String[] { "zebra", "Z:/temp/测试图/三个/城B.png" };
		// args = new String[] { "zebra", "Z:/temp/测试图/run"};

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
		case "zebra":
			// if (args.length == 2) {
			// int size = Integer.parseInt(args[1]);
			// if (size == 2048)
			// Zsheet3.maxSize = 2048;
			// }
			Zebra2Creator.process(input);
			break;
		case "zebra_clean":
			Zebra2Cleaner.clear(input);
			break;
		case "mask_check":
			MaskChecker.check(input);
			break;
		case "mask_strip":
			MaskStripper.strip(input);
			break;
		case "zmask": {
			String output = input.getParent() + "/" + FileUtil.getNoExt(input.getName()) + ".zmask";

			Zmask2 zmask = new Zmask2();
			zmask.load(input);

			byte[] zip = FileUtil.compress(zmask.toBytes());
			GalPack5 gp = new GalPack5();
			gp.setMaster(zip);
			zmask.extractMc5(gp.slaveSet);
			gp.toPack(output);
			gp.cleanUp();

			zmask.cleanUp();
		}
			break;
		case "zbase": {
			if (args.length != 3) {
				fail("java xx.jar zbase path 2(shrink level)");
				return;
			}

			int shrinkZ = Integer.parseInt(args[2]);

			String fileName = input.getName();
			fileName = FileUtil.getNoExt(fileName, true);
			String output = input.getParent() + "/" + fileName + ".zbase";

			ImageReader2 pt = ImageReader2.load(input.getPath(), Color.BLACK, true);
			Zbase zbase = new Zbase();
			zbase.load(pt, Color.black, true, shrinkZ);
			pt.dispose();

			byte[] data = zbase.toBytes();
			FileUtil.write(output, data);
		}
			break;
		case "zbase_planted": {
			String output = input.getParent() + "/" + FileUtil.getNoExt(input.getName()) + ".zbase.planted.png";
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
			String output = input.getParent() + "/" + FileUtil.getNoExt(input.getName(), true) + ".zway";

			int shrinkZ = Integer.parseInt(args[2]);

			ImageReader2 reader = ImageReader2.load(input.getPath(), Color.black, false);
			Zbase zbase = new Zbase();
			zbase.load(reader, Color.black, true, shrinkZ);
			reader.dispose();

			ZwayOld zway = new ZwayOld();
			zway.load(zbase);

			GalPack gpOut = new GalPack();
			byte[] zip = FileUtil.compress(zway.toBytes());
			gpOut.setMaster(zip);
			gpOut.writeTo(output);
			gpOut.cleanUp();

			// zway.draw(output + ".debug.png");
			zway.drawGravity(output + ".grav.png");
		}
			break;
		case "map": {
			String output = input.getParent() + "/" + FileUtil.getNoExt(input.getName(), true) + ".map";

			int shrinkZ = Integer.parseInt(args[2]);

			ImageReader2 reader = ImageReader2.load(input.getPath(), Color.black, false);
			ZbaseR br = new ZbaseR();
			br.load(reader, Color.black, true, shrinkZ);
			reader.dispose();

			RoadMap rm = new RoadMap();
			rm.load(br);

			BufferedImage way = ImageUtil.newImage(br.width, br.height, new Color(0x0, false));
			br.draw(way, 0, 0, 0);
			rm.busMap.draw(way);
			ImageIO.write(way, "png", new File(output + ".png"));

			FileUtil.write(output, rm.toBytes());
		}
			break;
		case "zpano": {
			String output = input.getParent() + "/" + input.getName() + ".zpano";
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
				MC5 mc5 = new MC5(levelB);// .isWindows(true).isAndroid(true).isIos(true).isCpu(true);
				GalFile.write(mc5.toString(), levelB);
				zb.writeUTFZ(mc5.toString());
				gp.addSlave(mc5.toString());
			}
			gp.setMaster(zb.toBytes());
			gp.writeTo(output);
			gp.cleanUp();
		}
			break;
		case "konggui":
			if (input.isFile()) {
				Konggui.toMaxtrix(input.toString());
			} else if (input.isDirectory()) {
				Arrays.stream(input.listFiles()).forEach(sub -> {
					try {
						Konggui.toMaxtrix(sub.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
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