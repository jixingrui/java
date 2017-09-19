package azura.banshee.zebra2.zimage2.large2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import azura.banshee.zebra2.zatlas2.ZGroup2;
import azura.banshee.zebra2.zatlas2.Zatlas2;
import azura.banshee.zebra2.zatlas2.Zframe2;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZimageTiny2 implements ZintCodecI {

	private int[] layerInAtlas;

	public void load(Zatlas2 atlas, BufferedImage source) {

		ArrayList<Integer> buffer = new ArrayList<Integer>();
		ZGroup2 group = atlas.newGroup();
		Zframe2 frame = group.newFrame();
		frame.load(source);
		buffer.add(frame.idxInAtlas);
		while (frame.isSmall() == false) {
			Zframe2 half = group.newFrame();
			half.loadHalf(frame);
			buffer.add(half.idxInAtlas);
			frame = half;
		}

		layerInAtlas = new int[buffer.size()];
		for (int i = 0; i < buffer.size(); i++) {
			layerInAtlas[i] = buffer.get(i);
		}
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		layerInAtlas = new int[reader.readZint()];
		for (int i = 0; i < layerInAtlas.length; i++) {
			layerInAtlas[i] = reader.readZint();
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(layerInAtlas.length);
		for (int i = 0; i < layerInAtlas.length; i++) {
			writer.writeZint(layerInAtlas[i]);
		}
	}

}
