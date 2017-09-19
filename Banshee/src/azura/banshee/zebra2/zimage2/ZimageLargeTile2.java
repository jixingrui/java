package azura.banshee.zebra2.zimage2;

import java.awt.image.BufferedImage;

import azura.banshee.chessboard.fi.TileFi;
import azura.banshee.zebra2.zatlas2.Zatlas2;
import azura.banshee.zebra2.zatlas2.Zframe2;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZimageLargeTile2 extends TileFi<ZimageLargeTile2> implements
		ZintCodecI {

	int frameIdxInAtlas;

	public ZimageLargeTile2(int fi, ZimageLarge2 pyramid) {
		super(fi, pyramid);
	}

	public void load(Zatlas2 atlas, BufferedImage land512) {
		Zframe2 frame = atlas.newGroup().newFrame();
		frame.load(land512);
		frameIdxInAtlas = frame.idxInAtlas;
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		frameIdxInAtlas = reader.readZint();
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(frameIdxInAtlas);
	}
}
