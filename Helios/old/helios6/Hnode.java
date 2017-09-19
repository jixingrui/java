package azura.helios6;

import org.apache.log4j.Logger;

public class Hnode {
	static Logger log = Logger.getLogger(Hnode.class);

	/**
	 * id==0 : new node and is not allowed in storage
	 * <p>
	 * id==-x : static tag node in Enum format
	 * <p>
	 * id==+x : dynamic node
	 */
	Long id;

	/**
	 * never null. empty=byte[0]
	 */
	private byte[] data = new byte[0];

	// ============== constructor ==============
	public Hnode() {
		id = 0L;
	}

	public Hnode(long id) {
		this.id = id;
		if (id == 0L)
			throw new Error("id==0 is reserved");
	}

	// public Hnode(byte[] bytes) {
	// this.data=bytes;
	// // fromBytes(bytes);
	// }

	public <E extends Enum<E>> Hnode(E tag) {
		id = (long) (-1 - tag.ordinal());
	}

	// ========== function ============

	public Long getId() {
		return id;
	}

	public int getIdInt() {
		return (int) (id & 0xffffffff);
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
		return "Hnode(id=" + id + ",size=" + data.length + "b)";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Hnode))
			throw new Error("type error");

		Hnode other = (Hnode) o;
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

	// public void eat(Hnode other) {
	// this.id = other.id;
	// this.data = other.data;
	// // this.version = other.version;
	// }

	// public byte[] toBytes() {
	// return data;
	// }

	// @Override
	// public void fromBytes(byte[] bytes) {
	// data = bytes;
	// }

}
