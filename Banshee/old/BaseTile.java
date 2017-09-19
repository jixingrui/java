package old;


import common.collections.bitset.lbs.LBSet;
import common.collections.buffer.ZintBuffer;

public class BaseTile {
	static final int white = 0xffffffff;
	static final int black = 0xff000000;
	static final int blue = 0xff0000ff;
	static final int green = 0xff00ff00;
	static final int magenta = 0xffff00ff;
	static final int yellow = 0xffffff00;
	static final int red = 0xffff0000;
	static final int cyan = 0xff00ffff;

	LBSet cBlack = new LBSet();
	LBSet cBlue = new LBSet();
	LBSet cGreen = new LBSet();
	LBSet cMagenta = new LBSet();
	LBSet cYellow = new LBSet();
	LBSet cRed = new LBSet();

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytes(cBlack.encode());
		zb.writeBytes(cBlue.encode());
		zb.writeBytes(cGreen.encode());
		zb.writeBytes(cMagenta.encode());
		zb.writeBytes(cYellow.encode());
		zb.writeBytes(cRed.encode());
		return zb.toBytes();
	}

	public static boolean isValid(int color) {
		return color == black || color == blue || color == green || color == magenta || color == yellow || color == red;
	}

	public boolean put(int color) {		
		switch (color) {
		case black: {
			cBlack.push(true);
			cBlue.push(false);
			cGreen.push(false);
			cMagenta.push(false);
			cYellow.push(false);
			cRed.push(false);
		}
			break;
		case blue: {
			cBlack.push(false);
			cBlue.push(true);
			cGreen.push(false);
			cMagenta.push(false);
			cYellow.push(false);
			cRed.push(false);
		}
			break;
		case green: {
			cBlack.push(false);
			cBlue.push(false);
			cGreen.push(true);
			cMagenta.push(false);
			cYellow.push(false);
			cRed.push(false);
		}
			break;
		case magenta: {
			cBlack.push(false);
			cBlue.push(false);
			cGreen.push(false);
			cMagenta.push(true);
			cYellow.push(false);
			cRed.push(false);
		}
			break;
		case yellow: {
			cBlack.push(false);
			cBlue.push(false);
			cGreen.push(false);
			cMagenta.push(false);
			cYellow.push(true);
			cRed.push(false);
		}
			break;
		case red: {
			cBlack.push(false);
			cBlue.push(false);
			cGreen.push(false);
			cMagenta.push(false);
			cYellow.push(false);
			cRed.push(true);
		}
			break;
		default: {
			cBlack.push(false);
			cBlue.push(false);
			cGreen.push(true);
			cMagenta.push(false);
			cYellow.push(false);
			cRed.push(false);
		}
			return false;
		}
		return true;
	}
}
