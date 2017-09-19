package azura.banshee.chessboard.base;

import common.collections.bitset.abs.ABSet;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintWriterI;

public class ByteMatrix {
	private int side;
	private ABSet abs;
	private byte[][] map;

	public ByteMatrix(int side) {
		this.side = side;
	}

	public void put(int x, int y, byte value) {
		if (x < 0 || x >= side || y < 0 || y >= side)
			return;

		getAbs().set(x * side + y, true);
		if (value != 0) {
			getMap()[x][y] = value;
		}
	}

	private ABSet getAbs() {
		if (abs == null)
			abs = new ABSet();
		return abs;
	}

	private byte[][] getMap() {
		if (map == null) {
			map = new byte[side][side];
			for (int i = 0; i < side; i++)
				map[i] = new byte[side];
		}
		return map;
	}

	byte[] encode() {
		ZintWriterI zb = new ZintBuffer();
		zb.writeBoolean(abs != null);
		if (abs != null)
			zb.writeBytesZ(abs.toBytes());
		zb.writeBoolean(map != null);
		if (map != null) {
			ZintWriterI mapZ = new ZintBuffer();
			for (int i = 0; i < side; i++)
				for (int j = 0; j < side; j++) {
					mapZ.writeByte(map[i][j]);
				}
			zb.writeBytesZ(mapZ.toBytes());
		}
		return zb.toBytes();
	}
}
