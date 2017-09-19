package common.collections.buffer.sa;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import common.collections.buffer.Zint;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintI;
import common.collections.buffer.i.ZintReaderI;

public class ZintReaderStream implements ZintReaderI {

	private InputStream is;
	private DataInputStream dis;
	private ZintI zintWrapper;

	public ZintReaderStream(InputStream is) {
		this.is = is;
		dis = new DataInputStream(is);
		zintWrapper = new ZintWrapperDIS(dis);
	}

	public void close() {
		try {
			dis.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int readZint() {
		return Zint.readIntZ(zintWrapper);
	}

	@Override
	public long readLongZ() {
		return Zint.readLongZ(zintWrapper);
	}

	@Override
	public long readLong() {
		try {
			return dis.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public String readUTFZ() {
		int length = readZint();
		if (length > 0) {
			byte[] data = new byte[length];
			try {
				dis.read(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new String(data, Charset.forName("UTF-8"));
		} else {
			return "";
		}
	}

	@Override
	public byte[] readBytesZ() {
		int length = readZint();
		if (length > 0) {
			byte[] result = new byte[length];
			try {
				dis.read(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return new byte[0];
		}
	}

	@Override
	public ZintBuffer readBytesZB() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytesZ(readBytesZ());
		return zb;
	}

	@Override
	public byte readByte() {
		try {
			return dis.readByte();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// @Override
	// public int readUnsignedByte() {
	// return dis.readUnsignedByte();
	// }

	@Override
	public boolean readBoolean() {
		try {
			return !(dis.readByte() == 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int readInt() {
		try {
			return dis.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public double readDouble() {
		try {
			return dis.readDouble();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean hasRemaining() {
		try {
			return dis.available() == 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public byte[] toBytes() {
		byte[] result = null;
		try {
			int length = dis.available();
			result = new byte[length];
			dis.read(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
