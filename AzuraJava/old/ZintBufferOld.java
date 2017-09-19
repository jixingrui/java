package common.collections.buffer.sa;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

import azura.fractale.netty.filter.ZintWrapperBB;
import common.collections.buffer.Zint;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;
import common.logger.Trace;

public class ZintBufferOld implements ZintReaderI, ZintWriterI {

	private ByteBuf buf;
	private ZintWrapperBB zintWrapper;

	public ZintBufferOld() {
		buf = Unpooled.buffer();
		zintWrapper = new ZintWrapperBB(buf);
	}

	public ZintBufferOld(byte[] bytes) {
		fromBytes(bytes);
	}

	public ZintBufferOld(ByteBuf in) {
		buf = in;
		zintWrapper = new ZintWrapperBB(buf);
	}

	public static void main(String[] args) {
		ZintBuffer encode = new ZintBuffer();
		encode.writeZint(0);
		encode.writeZint(19999);
		encode.writeBoolean(false);
		encode.writeZint(-1);
		encode.writeUTFZ("lala");
		byte[] b = new byte[] { 32, 58, -23 };
		encode.writeBytesZ(b);
		encode.writeZint(683);

		byte[] data = encode.toBytes();

		ZintReaderI decode = new ZintBuffer(data);

		Trace.trace(decode.readZint());
		Trace.trace(decode.readZint());
		Trace.trace(decode.readBoolean());
		Trace.trace(decode.readZint());
		Trace.trace(decode.readUTFZ());
		Trace.trace(decode.readBytesZ());
		Trace.trace(decode.readZint());
	}

	@Override
	public void writeZint(int value) {
		buf.writeBytes(Zint.zip(value));
	}

	@Override
	public int readZint() {
		return Zint.read(zintWrapper);
	}

	@Override
	public void writeLong(long value) {
		buf.writeLong(value);
	}

	@Override
	public long readLong() {
		return buf.readLong();
	}

	@Override
	public void writeUTFZ(String utf8) {
		if (utf8 != null) {
			byte[] utfBytes = utf8.getBytes(Charset.forName("UTF-8"));
			writeBytesZ(utfBytes);
		} else {
			writeZint(0);
		}
	}

	@Override
	public String readUTFZ() {
		int length = readZint();
		if (length > 0) {
			byte[] data = new byte[length];
			buf.readBytes(data);
			return new String(data, Charset.forName("UTF-8"));
		} else {
			return "";
		}
	}

	@Override
	public void writeBytesZ(byte[] b) {
		if (b == null) {
			writeZint(0);
		} else {
			writeZint(b.length);
			if (b.length < 1024) {
				buf.writeBytes(b);
			} else {
				int writerIndex = buf.writerIndex() + b.length;
				CompositeByteBuf comp = Unpooled.compositeBuffer();
				comp.addComponent(buf);
				comp.addComponent(Unpooled.wrappedBuffer(b));
				comp.writerIndex(writerIndex);

				buf = comp;
				zintWrapper = new ZintWrapperBB(buf);
			}
		}
	}

	@Override
	public byte[] readBytesZ() {
		int length = readZint();
		if (length > 0) {
			byte[] result = new byte[length];
			buf.readBytes(result);
			return result;
		} else {
			return new byte[0];
		}
	}

	@Override
	public byte readByte() {
		return buf.readByte();
	}

	@Override
	public void writeByte(int value) {
		buf.writeByte(value);
	}

	@Override
	public boolean readBoolean() {
		return buf.readByte() == 1;
	}

	@Override
	public void writeBoolean(boolean truth) {
		buf.writeByte(truth ? 1 : 0);
	}

	@Override
	public int readInt() {
		return buf.readInt();
	}

	@Override
	public void writeInt(int value) {
		buf.writeInt(value);
	}

	@Override
	public double readDouble() {
		return buf.readDouble();
	}

	@Override
	public void writeDouble(double value) {
		buf.writeDouble(value);
	}

	@Override
	public boolean hasRemaining() {
		return buf.readableBytes() == 0;
	}

	@Override
	public void fromBytes(byte[] bytes) {
		buf = Unpooled.wrappedBuffer(bytes);
		zintWrapper = new ZintWrapperBB(buf);
	}

	public byte[] toBytes() {
		byte[] result = new byte[buf.readableBytes()];
		buf.readBytes(result);
		close();
		return result;
	}

	@Override
	public void close() {
		buf.release();
	}

	@Override
	public ZintBuffer readBytesZB() {
		return null;
	}

	@Override
	public void writeBytesZB(ZintBuffer zb) {
	}

}
