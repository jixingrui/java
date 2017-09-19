package azura.banshee.main;

import java.io.File;
import java.io.IOException;

import azura.banshee.chessboard.base.PyramidBaseOld;
import azura.banshee.chessboard.dish.PyramidDishOld;
import azura.banshee.pano.PanoSize;
import azura.banshee.pano.PanoWriterAtf;
import azura.banshee.util.AtfE;
import azura.banshee.util.Konggui;
import azura.banshee.util.MaskChecker;
import azura.banshee.zimage.Zimage;
import azura.gallerid.GalPack;
import azura.gallerid.GalFile;

import common.collections.buffer.ZintBuffer;

public class Banshee16Old {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// args = new String[] { "base",
		// "D:\\executable\\maps\\e分店示范样本v11\\薛家岛v11\\大卧室v11\\大卧室_base_2.tif","2"
		// };
		// args = new String[] { "land_mask",
		// "z:\\sync\\image\\地图\\重庆路\\0" };
		// args = new String[] { "pano", "D:\\executable\\panorama\\crossing" };
		// args = new String[] { "zimage", "D:\\executable\\zimage\\target.tif"
		// };
		if (args.length < 2)
			System.out
					.println("java -jar Banshee.jar mode[mask_check,land_mask,base,naga,picture,zimage,pano,konggui] input");
		else {
			String mode = args[0];
			File input = new File(args[1]);
			if (!input.exists())
				return;

			switch (mode) {
			case "mask_check":
				MaskChecker.check(input);
				break;

			case "land_mask":
				File land = new File(input + "/land.tif");
				File mask = new File(input + "/mask.tif");
				if (land.exists() && mask.exists()) {
					PyramidDishOld pp = PyramidDishOld.load(input + "/land.tif",
							input + "/mask.tif");
					pp.writeToFile(input);
				} else {
					System.out
							.println("java -jar Banshee.jar land_mask folder(land.tif,mask.tif)");
					System.in.read();
				}
				break;

			case "base":
				if (args.length == 3) {
					int scale = (int) (Double.parseDouble(args[2]) * 100);
					if (scale >= 50 && scale <= 200) {
						PyramidBaseOld pb = PyramidBaseOld.load(input.getPath(),
								scale);
						pb.writeToFile(input);
					} else {
						System.out
								.println("java -jar Banshee.jar base base.tif [0.5-2]");
						System.in.read();
					}
				} else {
					System.out
							.println("java -jar Banshee.jar base base.tif [0.5-2]");
					System.in.read();
				}
				break;

			case "zanim": {
//				File[][] matrix = FileMatrix.assemble(input);
//
//				NagaWriter naga = new NagaWriter(matrix);
//				byte[] nagaZip = FileUtil.compress(naga.encode());
//
//				Gal_Pack gp = new Gal_Pack();
//				gp.setMaster(nagaZip);
//				naga.atlas.extractMd5(gp);
//				String outName = input.getParent() + "/" + input.getName()
//						+ ".zanim";
//				gp.encode(outName);
			}
				break;

			case "naga": {
//				File[][] matrix = FileMatrix.assemble(input);
//
//				NagaWriter naga = new NagaWriter(matrix);
//				byte[] nagaZip = FileUtil.compress(naga.encode());
//
//				Gal_Pack gp = new Gal_Pack();
//				gp.setMaster(nagaZip);
//				naga.atlas.extractMd5(gp);
//				String outName = input.getParent() + "/" + input.getName()
//						+ ".naga";
//				gp.encode(outName);
			}
				break;

			case "picture": {
				if (input.exists() && input.getName().endsWith(".tif")) {
//					PyramidLand pl = PyramidLand.load(input.getAbsolutePath());
//					pl.writeToFile(input);
//					Gal_File.cleanUp();
				} else {
					System.out.println("java -jar Banshee.jar picture xxx.tif");
					System.in.read();
				}
			}
				break;
			case "zimage": {
				if (input.exists()) {
					Zimage zl = new Zimage();
					zl.load(input.getAbsolutePath());
					zl.writeToFile(input);
//					Gal_File.cleanUp();
				} else {
					System.out
							.println("java -jar Banshee.jar zimage xxx.tif|xxx.png|xxx.jpg");
					System.in.read();
				}
			}
				break;
			case "pano": {
				byte[][][] data = PanoWriterAtf.write6(input);
				ZintBuffer zb = new ZintBuffer();
				GalPack gp = new GalPack();
				for (AtfE os : AtfE.values()) {
					for (PanoSize size : PanoSize.values()) {
						String md5 = GalFile.putData(data[os.ordinal()][size
								.ordinal()]);
						zb.writeUTF(md5);
						gp.addSlave(md5);
					}
				}
				gp.setMaster(zb.toBytes());

				String outName = input.getParent() + "/" + input.getName()
						+ ".zpano";
				gp.write(outName);
			}
				break;

			case "konggui":
				Konggui.toMaxtrix(input.toString());
				break;

			default:
				break;
			}
		}
//		Gal_File.cleanUp();
		System.out.println("\ndone");
	}

}
