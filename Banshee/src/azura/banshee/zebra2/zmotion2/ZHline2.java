package azura.banshee.zebra2.zmotion2;

import azura.banshee.util.FileMatrix2;
import azura.banshee.zebra2.RectC;
import azura.banshee.zebra2.Zebra2BranchI;
import azura.banshee.zebra2.zatlas2.Zatlas2;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZHline2 implements ZintCodecI, Zebra2BranchI {

	public int fps=12;
	public Zline2 hline = new Zline2();

	@Override
	public void readFrom(ZintReaderI reader) {
		fps = reader.readZint();
		hline.readFrom(reader);
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(fps);
		hline.writeTo(writer);
	}

	public void load(Zatlas2 atlas, FileMatrix2 fm) {
		hline.load(atlas, fm.matrix[0]);
	}

	@Override
	public RectC getBoundingBox() {
		return hline.getAverageBB();
	}

}
