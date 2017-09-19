package azura.expresso.rpc.phoenix13;

import java.util.concurrent.CompletableFuture;

import azura.expresso.Datum;
import azura.expresso.NameSpace;
import common.collections.IdStoreI;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class Rpc implements IdStoreI {

	public RpcE type;
	private int sessionId = -1;
	public int service, returnType;
	public Datum data;
	public CompletableFuture<Datum> returnFuture;

	protected Rpc(RpcE type) {
		this.type = type;
	}

	protected Rpc(NameSpace ns, ZintReaderI reader) throws RpcException {
		try {
			type = RpcE.values()[reader.readZint()];
			sessionId = reader.readZint();
			service = reader.readZint();
			int dataType = reader.readZint();
			if (dataType != -1) {
				data = ns.newDatum(dataType);
				data.readFrom(reader);
			}
			reader.close();
		} catch (Exception e) {
			throw new RpcException("decoding failed");
		}
	}

	@Override
	public int getId() {
		return sessionId;
	}

	@Override
	public void setId(int value) {
		sessionId = value;
	}

	public Datum getDatum(int type) throws RpcException {
		if (data == null || data.CLASSID() != type)
			throw new RpcException("wrong arg type");
		else
			return data;
	}

	public ZintReaderI toReader() {
		ZintBuffer zc = new ZintBuffer();
		zc.writeZint(type.ordinal());
		zc.writeZint(sessionId);
		zc.writeZint(service);
		if (data == null) {
			zc.writeZint(-1);
		} else {
			zc.writeZint(data.CLASSID());
			data.writeTo(zc);
		}
		return zc;
	}

	@Override
	public void readFrom(ZintReaderI reader) {
	}

	@Override
	public void writeTo(ZintWriterI writer) {
	}

	// public byte[] toBytes() {
	// ZintWriterI writer = new ZintBuffer();
	// writer.writeZint(type.ordinal());
	// writer.writeZint(sessionId);
	// writer.writeZint(service);
	// if (data == null) {
	// writer.writeZint(-1);
	// } else {
	// writer.writeZint(data.CLASSID());
	// data.writeTo(writer);
	// }
	// return writer.toBytes();
	// }
}
