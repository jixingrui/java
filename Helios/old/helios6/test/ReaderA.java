package azura.helios6.test;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class ReaderA implements BytesI{
	public int number;
	public String note;

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb=new ZintBuffer(bytes);
		number=zb.readZint();
		note=zb.readUTFZ();
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb=new ZintBuffer();
		zb.writeZint(number);
		zb.writeUTFZ(note);
		return zb.toBytes();
	}
	
	@Override
	public String toString() {
		return "number="+number+" note="+note;
	}

}
