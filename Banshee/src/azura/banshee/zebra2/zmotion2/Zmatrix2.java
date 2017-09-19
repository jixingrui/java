package azura.banshee.zebra2.zmotion2;

import java.util.ArrayList;
import java.util.List;

import azura.banshee.util.FileMatrix2;
import azura.banshee.zebra2.RectC;
import azura.banshee.zebra2.Zebra2BranchI;
import azura.banshee.zebra2.zatlas2.Zatlas2;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class Zmatrix2 implements ZintCodecI, Zebra2BranchI {
	public int fps = 12;
	public List<Zline2> rowList = new ArrayList<>();

	public void load(Zatlas2 atlas, FileMatrix2 fm) {

		for (int i = 0; i < fm.rows; i++) {
			Zline2 za = new Zline2();
			za.prepare(atlas, fm.matrix[i]);
			rowList.add(za);
		}

		rowList.parallelStream().forEach(za -> {
			try {
				za.load();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		fps = reader.readZint();
		int length = reader.readZint();
		for (int i = 0; i < length; i++) {
			Zline2 line = new Zline2();
			line.readFrom(reader);
			rowList.add(line);
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(fps);
		writer.writeZint(rowList.size());
		for (Zline2 line : rowList) {
			line.writeTo(writer);
		}
	}

	@Override
	public RectC getBoundingBox() {
		RectC bb = new RectC();
		int length=rowList.size();
		for(int i=0;i<length;i++){
			bb.add(rowList.get(i).getAverageBB());
		}
		bb.xc/=length;
		bb.yc/=length;
		bb.width/=length;
		bb.height/=length;
		return bb;
	}

}
