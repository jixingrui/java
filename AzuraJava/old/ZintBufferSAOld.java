package common.collections.buffer.sa;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import common.collections.buffer.Zint;
import common.collections.buffer.ZintI;
import common.collections.buffer.ZintReaderI;
import common.collections.buffer.ZintWriterI;
import common.logger.Trace;

public class ZintBufferSAOld implements ZintReaderI, ZintWriterI, ZintI {

	private static int reservation = 5;

	private ArrayList<ByteBuffer> list;
	private ByteBuffer current;
	private int idxCurrent = -1;
	private boolean read_write;

	public ZintBufferSAOld() {
		read_write = false;
		current = ByteBuffer.allocate(reservation);
	}

	public ZintBufferSAOld(byte[] bytes) {
		read_write = true;
		current = ByteBuffer.wrap(bytes);
	}

	public static void main(String[] args) {
		ZintBufferSAOld encode = new ZintBufferSAOld();
		encode.writeZint(0);
		encode.writeZint(19999);
		encode.writeBoolean(false);
		encode.writeZint(-1);
		encode.writeUTF("lala");
		byte[] b = new byte[] { 32, 58, -23, 58, -23, 58, -23, 58, -23, 58,
				-23, 58, -23, 58, -23, 58, -23, 58, -23, 58, -23, 58, -23, 58,
				-23 };
		encode.writeBytes(b);
		encode.writeZint(683);

		byte[] data = encode.toBytes();

		ZintReaderI decode = new ZintBufferSAOld(data);

		Trace.trace(decode.readZint());
		Trace.trace(decode.readZint());
		Trace.trace(decode.readBoolean());
		Trace.trace(decode.readZint());
		Trace.trace(decode.readUTF());
		Trace.trace(decode.readBytes());
		Trace.trace(decode.readZint());
	}

	public void push(ByteBuffer next) {
		ensureList();
		list.add(next);
		idxCurrent++;
		current = next;
	}

	private void ensureList() {
		if (list == null) {
			list = new ArrayList<>();
			list.add(current);
			idxCurrent = 0;
		}
	}

	private void ensureSpace(int space) {
		if (read_write == true)
			throw new Error();

		if (current.remaining() >= space) {// use current
			return;
		} else if (current.position() > reservation) {// use next
			push(ByteBuffer.allocate(space + reservation));
		} else {// expand current
			byte[] big = new byte[current.limit() + space + reservation];
			System.arraycopy(current.array(), 0, big, 0, current.limit());
			current = ByteBuffer.wrap(big);
			if (list != null) {
				list.set(idxCurrent, current);
			}
		}
	}

	private void checkMode() {
		if (read_write == false) {
			throw new Error("cannot read in write mode");
		}
	}

	// private void ensurePointer() {
	// if (current.remaining() <= 0) {
	// ensureList();
	// idxCurrent++;
	// current = list.get(idxCurrent);
	// }
	// }

	@Override
	public void writeBoolean(boolean truth) {
		ensureSpace(1);
		byte b = (byte) ((truth) ? 1 : 0);
		current.put(b);
	}

	@Override
	public void writeZint(int value) {
		ensureSpace(5);
		current.put(Zint.zip(value));
	}

	@Override
	public void writeInt(int value) {
		ensureSpace(4);
		current.putInt(value);
	}

	@Override
	public void writeLong(long value) {
		ensureSpace(8);
		current.putLong(value);
	}

	@Override
	public void writeDouble(double value) {
		ensureSpace(8);
		current.putDouble(value);
	}

	@Override
	public void writeUTF(String utf8) {
		if (utf8 != null) {
			byte[] utfBytes = utf8.getBytes(Charset.forName("UTF-8"));
			writeBytes(utfBytes);
		} else {
			writeZint(0);
		}
	}

	@Override
	public void writeByte(int value) {
		ensureSpace(1);
		current.put((byte) value);
	}

	@Override
	public void writeBytes(byte[] bytes) {
		if (read_write == true)
			throw new Error();

		if (bytes == null) {
			writeZint(0);
		} else {
			writeZint(bytes.length);
			if (current.limit() + bytes.length <= current.capacity()) {// has
				// room
				current.put(bytes);
			} else if (bytes.length > reservation) {// bytes too large: wrap
				push(ByteBuffer.wrap(bytes));
			} else if (current.capacity() <= reservation) {// both small: merge
				ensureSpace(bytes.length);
				current.put(bytes);
			} else {// current too large: allocate new
				push(ByteBuffer.allocate(bytes.length + reservation));
				current.put(bytes);
			}
		}
	}

	@Override
	public boolean readBoolean() {
		checkMode();
		return current.get() == 1;
	}

	@Override
	public int readZint() {
		checkMode();
		return Zint.read(this);
	}

	@Override
	public int readInt() {
		checkMode();
		return current.getInt();
	}

	@Override
	public long readLong() {
		checkMode();
		return current.getLong();
	}

	@Override
	public String readUTF() {
		int length = readZint();
		if (length > 0) {
			byte[] data = new byte[length];
			current.get(data);
			return new String(data, Charset.forName("UTF-8"));
		} else {
			return "";
		}
	}

	@Override
	public byte readByte() {
		checkMode();
		return current.get();
	}

	@Override
	public byte[] readBytes() {
		int length = readZint();

		if (length == 0)
			return new byte[0];

		byte[] result = new byte[length];
		current.get(result);
		return result;
	}

	@Override
	public double readDouble() {
		checkMode();
		return current.getDouble();
	}

	@Override
	public boolean isEmpty() {
		return current.remaining() == 0;
	}

	@Override
	public void fromBytes(byte[] bytes) {
		throw new Error("call constructor intead");
	}

	public byte[] toBytes() {
		byte[] result;
		if (list == null) {
			result = new byte[current.position()];
			System.arraycopy(current.array(), 0, result, 0, result.length);
			return result;
		}

		int length = 0;
		for (int i = 0; i < list.size(); i++) {
			length += list.get(i).position();
		}
		result = new byte[length];

		int writePointer = 0;
		for (int i = 0; i < list.size(); i++) {
			current = list.get(i);
			System.arraycopy(current.array(), 0, result, writePointer,
					current.position());
			writePointer += current.position();
		}
		return result;
	}

	@Override
	public void close() {
	}

	@Override
	public int readUnsignedByte() {
		return current.get() & 0xff;
	}

	@Override
	public void mark() {
		current.mark();
	}

	@Override
	public void rollBack() {
		current.reset();
	}

}
