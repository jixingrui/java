package azura.banshee.zebra2;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class RectC implements ZintCodecI {

	public double xc;
	public double yc;
	public double width;
	public double height;

	public void add(RectC other) {
		this.xc += other.xc;
		this.yc += other.yc;
		this.width += other.width;
		this.height += other.height;
	};

	@Override
	public String toString() {
		return "" + xc + "," + yc + "," + width + "," + height;
	};

	@Override
	public void readFrom(ZintReaderI reader) {
		xc = reader.readZint();
		yc = reader.readZint();
		width = reader.readZint();
		height = reader.readZint();
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint((int) xc);
		writer.writeZint((int) yc);
		writer.writeZint((int) width);
		writer.writeZint((int) height);
	}
}
