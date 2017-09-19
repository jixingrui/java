package azura.expresso.rpc;

import azura.expresso.Datum;
import azura.expresso.rpc.phoenix13.RpcE;
import common.collections.IdStoreI;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class Rpc implements IdStoreI {

	public RpcE type=RpcE.Single;
	private int sessionId = -1;
	public int service, returnType;
	private Datum datum;
	private RpcNodeA node;

	protected Rpc() {

	}

	public Rpc(RpcNodeA node) {
		this.node = node;
	}

	@Override
	public int getId() {
		return sessionId;
	}

	@Override
	public void setId(int value) {
//		if (id >= 0)
//			throw new Error("Rpc: cannot assign id twice");
//		if (value < 0)
//			throw new Error("Rpc: cannot assign negative id");
		sessionId = value;
	}

	public Datum getDatum() {
		return datum;
	}

	public void setDatum(int type, Datum datum) {
		this.datum = datum;
		if (datum.CLASSID() != type)
			throw new IllegalArgumentException("rpc datum wrong type");
	}

	public Datum getDatum(int type) {
		if (datum.CLASSID() != type)
			throw new IllegalArgumentException("rpc datum wrong type");
		return datum;
	}

	public byte[] toBytes() {
		ZintWriterI zb = new ZintBuffer();
		zb.writeZint(type.ordinal());
		zb.writeZint(sessionId);
		zb.writeZint(service);
		if (datum == null) {
			zb.writeZint(-1);
		} else {
			zb.writeZint(datum.CLASSID());
			datum.writeTo(zb);
		}
		return zb.toBytes();
	}

	public void readFrom(ZintReaderI reader) {
		type = RpcE.values()[reader.readZint()];
		sessionId = reader.readZint();
		service = reader.readZint();
		int dataType = reader.readZint();
		if (dataType != -1) {
			datum = node.ns.newDatum(dataType);
			datum.readFrom(reader);
		}
	}

	public void returnToRemote() {
		node.returnToRemote(this);
	}

	@Override
	public void writeTo(ZintWriterI writer) {
	}

}
