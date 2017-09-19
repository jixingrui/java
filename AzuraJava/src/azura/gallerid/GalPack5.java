package azura.gallerid;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import common.algorithm.MC5;
import common.algorithm.crypto.Rot;
import common.collections.buffer.ZintBuffer;

public class GalPack5 {
	private static final int version = 20170409;
	public String master;
	public Set<String> slaveSet = new LinkedHashSet<>();

	public void setMaster(byte[] data) {
		MC5 m = new MC5(data);
		master = m.toString();
		GalFile.write(master, data);
	}

	public byte[] getMaster() {
		return GalFile.read(master);
	}

	public void addSlave(String mc5) {
		if (mc5 == null || mc5.length() == 0)
			throw new Error();

		slaveSet.add(mc5);
	}

	private void fromIndex(byte[] index) {
		ZintBuffer zb = new ZintBuffer(index);
		int v = zb.readInt();
		if (v != version)
			throw new Error();

		int length = zb.readZint();
		master = zb.readUTFZ();
		for (int i = 0; i < length - 1; i++) {
			String mc5 = zb.readUTFZ();
			addSlave(mc5);
		}
	}

	private byte[] toIndex() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeInt(version);
		zb.writeZint(slaveSet.size() + 1);
		zb.writeUTFZ(master);
		for (String slave : slaveSet) {
			zb.writeUTFZ(slave);
		}
		return zb.toBytes();
	}

	private void writeOne(DataOutputStream dos, String mc5) throws IOException {
		byte[] data = GalFile.read(mc5);
		Rot.encrypt(data);
		dos.writeInt(data.length);
		dos.write(data);
	}

	private void readOne(DataInputStream dis, String mc5) throws IOException {
		int length = dis.readInt();
		byte[] data = new byte[length];
		dis.readFully(data);
		Rot.decrypt(data);
		GalFile.write(mc5, data);
	}

	public void toPack(String outputName) {
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try {
			fos = new FileOutputStream(outputName);
			dos = new DataOutputStream(fos);

			byte[] index = toIndex();
			dos.writeInt(index.length);
			dos.write(index);

			writeOne(dos, master);
			for (String slave : slaveSet) {
				writeOne(dos, slave);
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
		slaveSet.clear();

		FileInputStream fis = null;
		DataInputStream dis = null;
		boolean success = true;
		try {
			fis = new FileInputStream(input);
			dis = new DataInputStream(fis);

			int length = dis.readInt();
			byte[] index = new byte[length];
			dis.readFully(index);
			fromIndex(index);

			readOne(dis, master);
			for (String slave : slaveSet) {
				readOne(dis, slave);
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
	public static GalPack5 read(File input) {
		GalPack5 gp = new GalPack5();
		if (gp.loadFrom(input))
			return gp;
		else
			return null;
	}
}
