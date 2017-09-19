package azura.banshee.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import azura.banshee.util.AtfUtil;
import azura.gallerid.Gal_File;
import azura.gallerid.Gal_Pack;

import common.collections.buffer.ZintBuffer;

public class PanoWriterAtf {

	private static Gal_Pack gp = new Gal_Pack();
	
	public static void write(File folder) throws IOException {
		
		ZintBuffer zb=new ZintBuffer();
		write(zb,folder,"top");
		write(zb,folder,"bottom");
		write(zb,folder,"left");
		write(zb,folder,"right");
		write(zb,folder,"front");
		write(zb,folder,"back");
				
		gp.setMaster(zb.toBytes());
		String outName = folder.getParent() + "/" + folder.getName()
				+ ".pano";
		gp.encode(outName);
	}

	private static void write(ZintBuffer zb, File folder, String string) throws IOException {
		File face=new File(folder.getAbsolutePath()+"/"+string+".png");
		if(!face.exists())
			fail();

		BufferedImage img = ImageIO.read(face);
		if(img==null)
			fail();
		
		byte[][] data=AtfUtil.image2atf(img);
		String[] md5List=new String[3];
		md5List[0]=Gal_File.putData(data[0]);
		md5List[1]=Gal_File.putData(data[1]);
		md5List[2]=Gal_File.putData(data[2]);
		zb.writeUTF(md5List[0]);
		zb.writeUTF(md5List[1]);
		zb.writeUTF(md5List[2]);
		gp.addSlave(md5List[0]);
		gp.addSlave(md5List[1]);
		gp.addSlave(md5List[2]);
	}

	private static void fail() throws IOException {
		System.out.println("top,bottom,left,right,front,back: .png");
		System.in.read();
		System.exit(1);
	}
}
