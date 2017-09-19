package azura.banshee.chessboard.base;

import azura.banshee.chessboard.fi.TileFi;

import common.collections.buffer.ZintBuffer;

public class TileBaseOld extends TileFi<TileBaseOld> {

	private ByteMatrix top = new ByteMatrix(PyramidBaseOld.tileSide);
	private ByteMatrix flat = new ByteMatrix(PyramidBaseOld.tileSide);

	public TileBaseOld(int fi, PyramidBaseOld pyramid) {
		super(fi, pyramid);
	}

	public void setTop(int x, int y, byte value) {
		top.put(x, y, value);
	}

	public void setFlat(int x, int y, byte value) {
//		Trace.trace("put " + super.toString() + " " + value);
		flat.put(x, y, value);
	}

	byte[] encode() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytes(top.encode());
		zb.writeBytes(flat.encode());
		return zb.toBytes();
	}

	public void load(byte[] data) {
	}

}
