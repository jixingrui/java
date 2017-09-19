package azura.phoenix13.drop.karma.service; 
import java.util.concurrent.CompletableFuture;

import azura.expresso.Datum;
import azura.expresso.NameSpace;
import azura.expresso.rpc.phoenix13.Rpc;
import azura.expresso.rpc.phoenix13.RpcException;
import azura.expresso.rpc.phoenix13.RpcNodeA;
import azura.expresso.rpc.phoenix13.TunnelI;
public abstract class editCSA extends RpcNodeA{
	public editCSA(NameSpace ns,TunnelI tunnel){
		super(ns,tunnel);
	}
	@Override
	protected final void rpcIn(Rpc rpc)throws RpcException{
		switch(rpc.service){
		case 1158:
			hardHandler(rpc.getDatum(1159));
			break;
		case 1184:
			selectedIsHandler(rpc.getDatum(1185));
			break;
		default:
			throw new RpcException("unknown service type");
		}
	}

	/**
	 *@param arg1159
	 **/
	protected abstract void hardHandler(Datum arg1159);
	/**
	 *@param arg1185
	 **/
	protected abstract void selectedIsHandler(Datum arg1185);

	/**
	 *@param arg1155
	 **/
	protected final void hardCall(Datum arg1155){
		super.callRemote(1154, 1155, arg1155);
	}
	/**
	 *@return ret1164
	 **/
	protected final CompletableFuture<Datum> saveCall(){
		return super.callRemote(1162, 1164);
	}
	/**
	 *@param arg1167
	 **/
	protected final void loadCall(Datum arg1167){
		super.callRemote(1166, 1167, arg1167);
	}
	protected final void wipeCall(){
		super.callRemote(1170);
	}
	/**
	 *@return ret1175
	 **/
	protected final CompletableFuture<Datum> javaCall(){
		return super.callRemote(1173, 1175);
	}
	/**
	 *@return ret1179
	 **/
	protected final CompletableFuture<Datum> as3Call(){
		return super.callRemote(1177, 1179);
	}
	protected final void selectCall(){
		super.callRemote(1181);
	}
}