package azura.karma.editor.service;

import azura.expresso.NameSpace;
import azura.expresso.rpc.phoenix13.RpcException;
import azura.expresso.rpc.phoenix13.TunnelI;
import azura.fractale.netty.handler.FrackUserA;
import azura.phoenix13.drop.karma.Def_karma;

import common.collections.buffer.i.ZintReaderI;

public class KarmaNet extends FrackUserA implements TunnelI {

	// private static KarmaNet instance;
	//
	// public static KarmaNet singleton() {
	// return instance;
	// }

	public static NameSpace nsKarma = new NameSpace(Def_karma.data);

	public KarmaSC sc = new KarmaSC(this);

	// public KarmaNet() {
	// if (instance != null)
	// throw new Error();
	// instance = this;
	// }

	@Override
	public void tunnelOut(ZintReaderI reader) {
		socketSend(reader);
	}

	@Override
	public void connected() {
	}

	@Override
	public void disconnected() {
	}

	@Override
	public void socketReceive(ZintReaderI reader) {
		try {
			sc.tunnelIn(reader);
		} catch (RpcException e) {
			e.printStackTrace();
		}
	}

}
