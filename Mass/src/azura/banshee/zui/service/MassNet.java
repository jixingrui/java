package azura.banshee.zui.service;

import azura.expresso.NameSpace;
import azura.expresso.rpc.phoenix13.RpcException;
import azura.expresso.rpc.phoenix13.TunnelI;
import azura.fractale.netty.handler.FrackUserA;
import azura.phoenix13.drop.zui.Def_zui;

import common.collections.buffer.i.ZintReaderI;

public class MassNet extends FrackUserA implements TunnelI {

	public static NameSpace nsZui = new NameSpace(Def_zui.data);

	public MassSc sc = new MassSc(this);

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
