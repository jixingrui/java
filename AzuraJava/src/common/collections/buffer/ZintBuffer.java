package common.collections.buffer;

import java.nio.charset.Charset;
import java.util.ArrayList;

import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZintBuffer implements ZintReaderI, ZintWriterI {

	public static int MIN = 16;
	public static int MAX = 40;

	private ArrayList<ZintIO> list;
	private ZintIO writer, reader;
	private int readerIdx = -1;

	public ZintBuffer() {
	}

	public ZintBuffer(byte[] bytes) {
		fromBytes(bytes);
	}

//	public static void main(String[] args) {
//
//		ZintBuffer zb = new ZintBuffer();
//		zb.writeInt(3434);
//		zb.writeZint(0);
//		zb.writeZint(19999);
//		zb.writeUTFZ("");
//		zb.writeBoolean(false);
//		zb.writeZint(-1);
//		zb.writeUTFZ("lala");
//		zb.writeUTFZ("bubu");
//		byte[] b = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6,
//				7, 8, 9, 0, 1, 2, 3, 4, 5 };
//		zb.writeBytesZ(b);
//		zb.writeLong(0x393892l);
//		zb.writeZint(683);
//
//		ZintBuffer shell = new ZintBuffer();
//		shell.writeUTFZ("this is header");
//		shell.writeBytesZB(zb);
//
//		Trace.trace(shell.readUTFZ());
//		zb = shell.readBytesZB();
//
//		Trace.trace(zb.readInt());
//		Trace.trace(zb.readZint());
//		Trace.trace(zb.readZint());
//		Trace.trace(zb.readUTFZ());
//		Trace.trace(zb.readBoolean());
//		Trace.trace(zb.readZint());
//		Trace.trace(zb.readUTFZ());
//		Trace.trace(zb.readUTFZ());
//		Trace.trace(zb.readBytesZ());
//		Trace.trace(zb.readLong());
//		Trace.trace(zb.readZint());
//	}

	// =========================== list =====================

	private int readableBytes() {
		int length = 0;
		if (list == null) {
			return getReader().readableBytes();
		} else {
			for (int i = 0; i < list.size(); i++) {
				length += list.get(i).readableBytes();
			}
		}
		return length;
	}

	private void push(ZintIO next) {
		ensureList();
		list.add(next);
		writer = next;
	}

	private void ensureList() {
		if (list == null) {
			list = new ArrayList<>();
			list.add(writer);
		}
	}

	private ZintIO getWriter(int space) {
		if (writer == null) {
			writer = ZintIO.allocate(space + MIN);
		} else if (writer.writeableBytes() >= space) {// has room: use current
			return writer;
		} else if (writer.dataSize() > MAX) {// data too large: append new
			push(ZintIO.allocate(space + MIN));
		} else {// expand current
			writer.expand(space + MIN);
		}
		return writer;
	}

	private ZintIO getReader() {
		if (reader == null) {
			if (list == null)
				reader = writer;
			else {
				readerIdx = 0;
				reader = list.get(readerIdx);
			}
		} else if (reader.readableBytes() == 0 && list != null
				&& readerIdx < list.size() - 1) {
			readerIdx++;
			reader = list.get(readerIdx);
		}
		return reader;
	}

	public void resetReader() {
		if (list == null) {
			reader = writer;
			if (reader != null)
				reader.readerIndex = 0;
			return;
		}

		readerIdx = 0;
		reader = list.get(readerIdx);
		for (ZintIO zio : list) {
			zio.readerIndex = 0;
		}
	}

	// =========================== write =====================
	@Override
	public void writeBoolean(boolean value) {
		getWriter(1).writeBoolean(value);
	}

	@Override
	public void writeZint(int value) {
		getWriter(5).writeIntZ(value);
	}

	@Override
	public void writeInt(int value) {
		getWriter(4).writeInt(value);
	}

	@Override
	public void writeLongZ(long value) {
		getWriter(9).writeLongZ(value);
	}

	@Override
	public void writeLong(long value) {
		getWriter(8).writeLong(value);
	}

	@Override
	public void writeDouble(double value) {
		getWriter(8).writeDouble(value);
	}

	@Override
	public void writeUTFZ(String utf8) {
		if (utf8 == null)
			utf8 = "";
		byte[] utfBytes = utf8.getBytes(Charset.forName("UTF-8"));
		writeBytesZ(utfBytes);
	}

	@Override
	public void writeByte(int value) {
		getWriter(1).writeByte(value);
	}

	@Override
	public void writeBytesZ(byte[] tail) {
		if (tail == null)
			tail = new byte[0];
		writeZint(tail.length);
		writePiece(ZintIO.wrap(LogicalArray.wrap(tail)));
	}

	public void writeBytesZB(ZintBuffer src) {
		writeZint(src.readableBytes());
		while (src.getReader().readableBytes() > 0) {
			writePiece(src.reader);
		}
	}

	private void writePiece(ZintIO next) {
		if (writer == null) {
			writer = next.readPart(next.readableBytes());
		} else if (next.readableBytes() == 0) {
			return;
		} else if (writer.writeableBytes() >= next.readableBytes()) {
			// has room: write
			writer.copyFrom(next);
		} else if (writer.dataSize() + next.readableBytes() < MAX) {
			// small:merge
			LogicalArray combo = LogicalArray.allocate(writer.dataSize()
					+ next.readableBytes() + MIN);
			LogicalArray.copy(writer.array, 0, combo, 0, writer.dataSize());
			LogicalArray.copy(next.array, next.readerIndex, combo,
					writer.dataSize(), next.readableBytes());
			int ri = writer.readerIndex;
			int wi = writer.dataSize() + next.readableBytes();
			writer = ZintIO.wrap(combo);
			writer.readerIndex = ri;
			writer.writerIndex = wi;
			if (list != null)
				list.set(list.size() - 1, writer);
		} else {
			// large: link
			push(next.readPart(next.readableBytes()));
		}
		next.readerIndex = next.writerIndex;
	}

	@Override
	public byte[] readBytesZ() {
		return readBytesZB().toBytes();
	}

	public ZintBuffer readBytesZB() {
		int length = readZint();

		ZintBuffer result = new ZintBuffer();

		while (length > 0) {
			if (length > getReader().readableBytes()) {
				if (reader.readableBytes() == 0)
					throw new Error("ZintBuffer: readable length is wrong value");
				length -= reader.readableBytes();
				result.writePiece(reader.readPart(reader.readableBytes()));
			} else {
				result.writePiece(reader.readPart(length));
				length = 0;
			}
		}

		return result;
	}

	// =========================== read =====================
	@Override
	public boolean readBoolean() {
		return getReader().readBoolean();
	}

	@Override
	public int readZint() {
		return Zint.readIntZ(getReader());
	}

	@Override
	public int readInt() {
		return getReader().readInt();
	}

	@Override
	public long readLongZ() {
		return getReader().readLongZ();
	}

	@Override
	public long readLong() {
		return getReader().readLong();
	}

	@Override
	public String readUTFZ() {
		byte[] data = readBytesZB().toBytes();
		return new String(data, Charset.forName("UTF-8"));
	}

	@Override
	public byte readByte() {
		return getReader().readByte();
	}

	@Override
	public double readDouble() {
		return getReader().readDouble();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		writer = ZintIO.wrap(LogicalArray.wrap(bytes));
		reader = writer;
		list = null;
		readerIdx = -1;
	}

	@Override
	public byte[] toBytes() {
		resetReader();

		if (reader == null) {
			return new byte[0];
		}

		LogicalArray result;
		if (list == null) {
			int size = reader.dataSize();
			LogicalArray sub = reader.array.subArray(0, size);
			return sub.toBytes();
		}

		int length = 0;
		for (int i = 0; i < list.size(); i++) {
			length += list.get(i).dataSize();
		}
		result = LogicalArray.allocate(length);

		int writePointer = 0;
		for (readerIdx = 0; readerIdx < list.size(); readerIdx++) {
			reader = list.get(readerIdx);
			LogicalArray.copy(reader.array, 0, result, writePointer,
					reader.dataSize());
			writePointer += reader.dataSize();
		}
		readerIdx--;
		return result.toBytes();
	}

	@Override
	public boolean hasRemaining() {
		if (reader.readableBytes() > 0) {
			return true;
		} else if (list != null && readerIdx < list.size() - 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void close() {
		list = null;
		writer = null;
		reader = null;
		readerIdx = -1;
	}

}
