package azura.expresso.rpc.phoenix13;

import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;

import azura.expresso.Datum;
import azura.expresso.NameSpace;

import common.collections.IdStore;
import common.collections.buffer.i.ZintReaderI;

public abstract class RpcNodeA {

	protected static Logger log = Logger.getLogger(RpcNodeA.class);

	protected abstract void rpcIn(Rpc rpc) throws RpcException;

	public abstract void connected();

	public abstract void disconnected();

	private final IdStore<Rpc> session_Rpc = new IdStore<>(10);

	final protected NameSpace ns;
	final private TunnelI tunnel;

	public RpcNodeA(NameSpace ns, TunnelI tunnelOut) {
		this.ns = ns;
		this.tunnel = tunnelOut;
	}

	final protected void callRemote(int serviceType) {
		Rpc rpc = new Rpc(RpcE.Single);
		rpc.service = serviceType;
		tunnel.tunnelOut(rpc.toReader());
	}

	final protected void callRemote(int serviceType, int argType, Datum arg) {
		if (argType != arg.CLASSID())
			throw new IllegalArgumentException();

		Rpc rpc = new Rpc(RpcE.Single);
		rpc.service = serviceType;
		rpc.data = arg;
		tunnel.tunnelOut(rpc.toReader());
	}

	final protected CompletableFuture<Datum> callRemote(int serviceType,
			int returnType) {

		Rpc rpc = new Rpc(RpcE.Bi_Call);
		session_Rpc.add(rpc);
		rpc.service = serviceType;
		rpc.returnType = returnType;
		rpc.returnFuture = new CompletableFuture<>();
		tunnel.tunnelOut(rpc.toReader());
		return rpc.returnFuture;
	}

	final protected CompletableFuture<Datum> callRemote(int serviceType,
			int argType, Datum arg, int returnType) {
		if (arg.CLASSID() != argType)
			throw new IllegalArgumentException(this.getClass().getSimpleName()
					+ ": rpc arg type not match");

		Rpc rpc = new Rpc(RpcE.Bi_Call);
		session_Rpc.add(rpc);
		rpc.service = serviceType;
		rpc.data = arg;
		rpc.returnType = returnType;
		rpc.returnFuture = new CompletableFuture<>();
		tunnel.tunnelOut(rpc.toReader());
		return rpc.returnFuture;
	}

	final public void tunnelIn(ZintReaderI reader) throws RpcException {
		Rpc rpc = new Rpc(this.ns, reader);
		switch (rpc.type) {
		case Bi_Call: {
			rpc.returnFuture = new CompletableFuture<>();
			rpc.returnFuture.thenAccept(data -> {
				rpc.data = data;
				rpc.type = RpcE.Bi_Return;
				tunnel.tunnelOut(rpc.toReader());
			});
		}
		case Single:
			rpcIn(rpc);
			break;
		case Bi_Return: {
			Rpc old = session_Rpc.remove(rpc.getId());
			if (old == null)
				throw new RpcException("return session not found");
			if (old.returnType != -1
					&& (rpc.data == null || rpc.data.CLASSID() != old.returnType))
				throw new RpcException("return data invalid");

			old.returnFuture.complete(rpc.data);
		}
			break;

		default:
			throw new RpcException("invalid type");
		}
	}

}
