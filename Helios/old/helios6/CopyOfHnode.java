package azura.helios6;

import org.apache.log4j.Logger;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintReaderI;

public class CopyOfHnode implements BytesI {
	static Logger log = Logger.getLogger(CopyOfHnode.class);

	/**
	 * id==0 : new node and is not allowed in storage
	 * <p>
	 * id==-x : static tag node in Enum format
	 * <p>
	 * id==+x : dynamic node
	 */
	Long id;
	int version;

	/**
	 * never null. empty=byte[0]
	 */
	private byte[] data = new byte[0];

	// ============== constructor ==============
	public CopyOfHnode() {
		id = 0L;
	}

	public CopyOfHnode(long id) {
		this.id = id;
		if (id == 0L)
			throw new Error("id==0 is reserved");
	}

	public CopyOfHnode(byte[] bytes) {
		fromBytes(bytes);
	}

	public <E extends Enum<E>> CopyOfHnode(E tag) {
		id = (long) (-1 - tag.ordinal());
	}

	// ========== function ============

	public Long getId() {
		return id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		if (data == null)
			data = new byte[0];
		this.data = data;
	}

	@Override
	public String toString() {
		return "Hnode(id=" + id + ",version=" + version + ",size="
				+ data.length + "bytes)";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof CopyOfHnode))
			throw new Error("type error");

		CopyOfHnode other = (CopyOfHnode) o;
		if (this == other)
			return true;
		else if (this.id == 0 || other.id == 0)
			return false;
		else
			return this.id.equals(other.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public void eat(CopyOfHnode other) {
		this.id = other.id;
		this.data = other.data;
		this.version = other.version;
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeLong(id);
		zb.writeInt(version);
		zb.writeBytesZ(data);
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		try {
			ZintReaderI zb = new ZintBuffer(bytes);
			this.id = zb.readLong();
			this.version = zb.readInt();
			this.data = zb.readBytesZ();
		} catch (Exception e) {
			throw new Error("Hnode decoding error");
		} finally {
		}
	}

}
