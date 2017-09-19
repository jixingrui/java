package azura.helios5;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.mapdb.Serializer;

import azura.helios5.join.Roller;

import common.algorithm.FastMath;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintReaderI;

/**
 * weight is impossible. See {@link Roller}
 */
@SuppressWarnings("serial")
public class HeliosNode implements Serializer<HeliosNode>, Serializable, BytesI {
	static Logger log = Logger.getLogger(HeliosNode.class);

	/**
	 * 0 means new node and is not allowed in storage - means static tag node in
	 * Enum format + means dynamic node
	 */
	public long id = 0;
	/**
	 * used by the user to store a reference to a node for direct access
	 */
	public String uid = "";
	/**
	 * Before change, check if the requested timeStamp is the same as the
	 * original. Change includes data change and link change.
	 */
	public volatile long timeStamp;
	/**
	 * never null. empty=byte[0]
	 */
	public volatile byte[] data=new byte[0];

	public HeliosNode() {
	}

	public HeliosNode(String uid) {
		this.uid = uid;
	}

	public HeliosNode(long id) {
		this.id = id;
	}

	public HeliosNode(byte[] id_data) {
		fromBytes(id_data);
	}

	public <E extends Enum<E>> HeliosNode(E tag) {
		id = (-1 - tag.ordinal());
	}

	public HeliosNode stamp() {
		timeStamp = FastMath.tidLong();
		return this;
	}

	public boolean matchTime(HeliosNode old) {
		return old != null && this.id == old.id
				&& this.timeStamp == old.timeStamp;
	}

	@Override
	public String toString() {
		return "Node(" + id + ":" + timeStamp + ")";
	}

	@Override
	public void fromBytes(byte[] id_data) {
		ZintReaderI zb = new ZintBuffer(id_data);
		this.id = zb.readLong();
		this.uid = zb.readUTFZ();
		this.timeStamp = zb.readLong();
		this.data = zb.readBytesZ();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * id_data
	 */
	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeLong(id);
		zb.writeUTFZ(uid);
		zb.writeLong(timeStamp);
		zb.writeBytesZ(data);
		return zb.toBytes();
	}

	public HeliosNode clone() {
		HeliosNode c = new HeliosNode(id);
		c.uid = this.uid;
		c.timeStamp = this.timeStamp;
		c.data = Arrays.copyOf(data, data.length);
		return c;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof HeliosNode))
			throw new Error("type error");

		HeliosNode other = (HeliosNode) o;
		if (this == other)
			return true;
		else if (this.id == 0 || other.id == 0)
			return false;
		else
			return this.id == other.id;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

	@Override
	public void serialize(DataOutput out, HeliosNode value) throws IOException {
		byte[] data = value.toBytes();
		out.writeInt(data.length);
		out.write(data);
		// log.debug("serialized " + data.length);
	}

	@Override
	public HeliosNode deserialize(DataInput in, int available)
			throws IOException {
		int length = in.readInt();
		// log.debug("deserialized " + length);
		byte[] data = new byte[length];
		in.readFully(data);
		HeliosNode node = new HeliosNode();
		node.fromBytes(data);
		return node;
	}

	@Override
	public int fixedSize() {
		return 0;
	}

//	public int dataLength() {
//		if (data == null)
//			return 0;
//		else
//			return data.length;
//	}
}
