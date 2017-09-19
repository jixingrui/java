package azura.expresso.rpc;

import azura.expresso.NameSpace;
import azura.expresso.rpc.phoenix13.RpcE;

import common.collections.IdStore;
import common.collections.buffer.i.ZintReaderI;
import common.logger.Trace;

public abstract class RpcNodeA {
	private static IdStore<Rpcr<?>> session_Rpcr = new IdStore<Rpcr<?>>(10);

	final protected NameSpace ns;
	protected RpcTunnelI tunnelOut;

	public RpcNodeA(NameSpace ns, RpcTunnelI tunnelOut) {
		this.ns = ns;
		this.tunnelOut = tunnelOut;
	}

	final public void callToRemote(Rpc rpc, int service) {
		rpc.service = service;
		tunnelOut(rpc);
	}

	final protected void callToRemote(Rpcr<?> rpcShell, int service) {
		rpcShell.type = RpcE.Bi_Call;
		session_Rpcr.add(rpcShell);
		callToRemote((Rpc) rpcShell, service);
	}

	final void returnToRemote(Rpc rpcWithReturn) {
		rpcWithReturn.type = RpcE.Bi_Return;
		tunnelOut(rpcWithReturn);
	}

	private void tunnelOut(Rpc rpc) {
		tunnelOut.tunnelOutIn(rpc.toBytes());
	}

	final private void tunnelReceive(Rpc rpc) {
		if (rpc.type == RpcE.Bi_Return) {
			Rpcr<?> shell = session_Rpcr.remove(rpc.getId());
			if (shell == null) {
				Trace.trace("RpcNodeA: session not exist: " + rpc.getId());
				return;
			}

			shell.returnHandler(rpc.getDatum());
		} else {
			serve(rpc);
		}
	}

	final public void tunnelIn(ZintReaderI reader) {
		Rpc rpc = new Rpc(this);
		rpc.readFrom(reader);
		reader.close();

		this.tunnelReceive(rpc);
	}

	protected abstract void serve(Rpc rpcWithArg);

	public abstract void connected();

	public abstract void disconnected();
}
