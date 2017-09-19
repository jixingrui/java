package azura.gallerid;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import common.algorithm.MC5;
import common.algorithm.crypto.Rot;

public class GalPack {
	public String master;
	public Set<String> slaveSet = new HashSet<String>();

	public void setMaster(byte[] data) {
		MC5 m = new MC5(data);
		master = m.toString();
		GalFile.write(master, data);
	}

	public byte[] getMaster() {
		return GalFile.read(master);
	}

	public void addSlave(String me5) {
		if (me5 == null || me5.length() == 0)
			throw new Error();

		slaveSet.add(me5);
	}

	public void writeTo(String outputName) {
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try {
			fos = new FileOutputStream(outputName);
			dos = new DataOutputStream(fos);

			dos.writeInt(slaveSet.size() + 1);
			dos.writeUTF(master);
			byte[] data = GalFile.read(master);
			Rot.encrypt(data);
			dos.writeInt(data.length);
			dos.write(data);
			for (String slave : slaveSet) {
				dos.writeUTF(slave);
				data = GalFile.read(slave);
				Rot.encrypt(data);
				dos.writeInt(data.length);
				dos.write(data);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// cleanUp();
				dos.flush();
				fos.flush();
				dos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param input
	 * @return success
	 */
	private boolean loadFrom(File input) {
		FileInputStream fis = null;
		DataInputStream dis = null;
		boolean success = true;
		try {
			fis = new FileInputStream(input);
			dis = new DataInputStream(fis);

			int size = dis.readInt();
			master = dis.readUTF();
			int length = dis.readInt();
			byte[] data = new byte[length];
			dis.read(data);
			Rot.decrypt(data);
			GalFile.write(master, data);
			for (int i = 1; i < size; i++) {
				String slave = dis.readUTF();
				slaveSet.add(slave);
				length = dis.readInt();
				data = new byte[length];
				dis.read(data);
				Rot.decrypt(data);
				GalFile.write(slave, data);
			}
		} catch (Exception e) {
			success = false;
		} finally {
			try {
				dis.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!success) {
			master = null;
			slaveSet.clear();
		}
		return success;
	}

	public void cleanUp() {
		GalFile.deleteData(master);
		for (String slave : slaveSet)
			GalFile.deleteData(slave);
	}

	/**
	 * @param input
	 * @return null if fail
	 */
	public static GalPack read(File input) {
		GalPack gp = new GalPack();
		if (gp.loadFrom(input))
			return gp;
		else
			return null;
	}
}
