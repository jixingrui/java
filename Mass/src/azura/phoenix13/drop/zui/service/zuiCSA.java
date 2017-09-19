package azura.phoenix13.drop.zui.service; 
import java.util.concurrent.CompletableFuture;

import azura.expresso.Datum;
import azura.expresso.NameSpace;
import azura.expresso.rpc.phoenix13.Rpc;
import azura.expresso.rpc.phoenix13.RpcException;
import azura.expresso.rpc.phoenix13.RpcNodeA;
import azura.expresso.rpc.phoenix13.TunnelI;
public abstract class zuiCSA extends RpcNodeA{
	public zuiCSA(NameSpace ns,TunnelI tunnel){
		super(ns,tunnel);
	}
	@Override
	protected final void rpcIn(Rpc rpc)throws RpcException{
		switch(rpc.service){
		case 1076:
			hardHandler(rpc.getDatum(1077));
			break;
		case 1101:
			tellActionHandler(rpc.getDatum(1102));
			break;
		case 1148:
			tellScreenSettingHandler(rpc.getDatum(1149));
			break;
		default:
			throw new RpcException("unknown service type");
		}
	}

	/**
	 *@param arg1077
	 **/
	protected abstract void hardHandler(Datum arg1077);
	/**
	 *@param arg1102
	 **/
	protected abstract void tellActionHandler(Datum arg1102);
	/**
	 *@param arg1149
	 **/
	protected abstract void tellScreenSettingHandler(Datum arg1149);

	/**
	 *@param arg1073
	 **/
	protected final void hardCall(Datum arg1073){
		super.callRemote(1072, 1073, arg1073);
	}
	/**
	 *@return ret1082
	 **/
	protected final CompletableFuture<Datum> saveCall(){
		return super.callRemote(1080, 1082);
	}
	/**
	 *@param arg1085
	 **/
	protected final void loadCall(Datum arg1085){
		super.callRemote(1084, 1085, arg1085);
	}
	protected final void wipeCall(){
		super.callRemote(1088);
	}
	protected final void setTargetCall(){
		super.callRemote(1091);
	}
	/**
	 *@return ret1096
	 **/
	protected final CompletableFuture<Datum> reportActionCall(){
		return super.callRemote(1094, 1096);
	}
	/**
	 *@param arg1098
	 **/
	protected final void selectByActionCall(Datum arg1098){
		super.callRemote(1097, 1098, arg1098);
	}
	/**
	 *@param arg1106
	 **/
	protected final void saveMsgCall(Datum arg1106){
		super.callRemote(1105, 1106, arg1106);
	}
	/**
	 *@param arg1121
	 **/
	protected final void setScreenSettingCall(Datum arg1121){
		super.callRemote(1120, 1121, arg1121);
	}
	/**
	 *@return ret1126
	 **/
	protected final CompletableFuture<Datum> getScreenSettingCall(){
		return super.callRemote(1124, 1126);
	}
	/**
	 *@param arg1133
	 **/
	protected final void selectToActionCall(Datum arg1133){
		super.callRemote(1132, 1133, arg1133);
	}
}