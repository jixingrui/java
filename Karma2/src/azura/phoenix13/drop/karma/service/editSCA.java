package azura.phoenix13.drop.karma.service; 
import java.util.concurrent.CompletableFuture;

import azura.expresso.Datum;
import azura.expresso.NameSpace;
import azura.expresso.rpc.phoenix13.Rpc;
import azura.expresso.rpc.phoenix13.RpcException;
import azura.expresso.rpc.phoenix13.RpcNodeA;
import azura.expresso.rpc.phoenix13.TunnelI;
public abstract class editSCA extends RpcNodeA{
	public editSCA(NameSpace ns,TunnelI tunnel){
		super(ns,tunnel);
	}
	@Override
	protected final void rpcIn(Rpc rpc)throws RpcException{
		switch(rpc.service){
		case 1154:
			hardHandler(rpc.getDatum(1155));
			break;
		case 1162:
			saveHandler(rpc.returnFuture);
			break;
		case 1166:
			loadHandler(rpc.getDatum(1167));
			break;
		case 1170:
			wipeHandler();
			break;
		case 1173:
			javaHandler(rpc.returnFuture);
			break;
		case 1177:
			as3Handler(rpc.returnFuture);
			break;
		case 1181:
			selectHandler();
			break;
		default:
			throw new RpcException("unknown service type");
		}
	}

	/**
	 *@param arg1155
	 **/
	protected abstract void hardHandler(Datum arg1155);
	/**
	 *@param ret1164Sink ret1164
	 **/
	protected abstract void saveHandler(CompletableFuture<Datum> ret1164Sink);
	/**
	 *@param arg1167
	 **/
	protected abstract void loadHandler(Datum arg1167);
	protected abstract void wipeHandler();
	/**
	 *@param ret1175Sink ret1175
	 **/
	protected abstract void javaHandler(CompletableFuture<Datum> ret1175Sink);
	/**
	 *@param ret1179Sink ret1179
	 **/
	protected abstract void as3Handler(CompletableFuture<Datum> ret1179Sink);
	protected abstract void selectHandler();

	/**
	 *@param arg1159
	 **/
	protected final void hardCall(Datum arg1159){
		super.callRemote(1158, 1159, arg1159);
	}
	/**
	 *@param arg1185
	 **/
	protected final void selectedIsCall(Datum arg1185){
		super.callRemote(1184, 1185, arg1185);
	}
}