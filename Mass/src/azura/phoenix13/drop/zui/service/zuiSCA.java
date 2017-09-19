package azura.phoenix13.drop.zui.service; 
import java.util.concurrent.CompletableFuture;

import azura.expresso.Datum;
import azura.expresso.NameSpace;
import azura.expresso.rpc.phoenix13.Rpc;
import azura.expresso.rpc.phoenix13.RpcException;
import azura.expresso.rpc.phoenix13.RpcNodeA;
import azura.expresso.rpc.phoenix13.TunnelI;
public abstract class zuiSCA extends RpcNodeA{
	public zuiSCA(NameSpace ns,TunnelI tunnel){
		super(ns,tunnel);
	}
	@Override
	protected final void rpcIn(Rpc rpc)throws RpcException{
		switch(rpc.service){
		case 1072:
			hardHandler(rpc.getDatum(1073));
			break;
		case 1080:
			saveHandler(rpc.returnFuture);
			break;
		case 1084:
			loadHandler(rpc.getDatum(1085));
			break;
		case 1088:
			wipeHandler();
			break;
		case 1091:
			setTargetHandler();
			break;
		case 1094:
			reportActionHandler(rpc.returnFuture);
			break;
		case 1097:
			selectByActionHandler(rpc.getDatum(1098));
			break;
		case 1105:
			saveMsgHandler(rpc.getDatum(1106));
			break;
		case 1120:
			setScreenSettingHandler(rpc.getDatum(1121));
			break;
		case 1124:
			getScreenSettingHandler(rpc.returnFuture);
			break;
		case 1132:
			selectToActionHandler(rpc.getDatum(1133));
			break;
		default:
			throw new RpcException("unknown service type");
		}
	}

	/**
	 *@param arg1073
	 **/
	protected abstract void hardHandler(Datum arg1073);
	/**
	 *@param ret1082Sink ret1082
	 **/
	protected abstract void saveHandler(CompletableFuture<Datum> ret1082Sink);
	/**
	 *@param arg1085
	 **/
	protected abstract void loadHandler(Datum arg1085);
	protected abstract void wipeHandler();
	protected abstract void setTargetHandler();
	/**
	 *@param ret1096Sink ret1096
	 **/
	protected abstract void reportActionHandler(CompletableFuture<Datum> ret1096Sink);
	/**
	 *@param arg1098
	 **/
	protected abstract void selectByActionHandler(Datum arg1098);
	/**
	 *@param arg1106
	 **/
	protected abstract void saveMsgHandler(Datum arg1106);
	/**
	 *@param arg1121
	 **/
	protected abstract void setScreenSettingHandler(Datum arg1121);
	/**
	 *@param ret1126Sink ret1126
	 **/
	protected abstract void getScreenSettingHandler(CompletableFuture<Datum> ret1126Sink);
	/**
	 *@param arg1133
	 **/
	protected abstract void selectToActionHandler(Datum arg1133);

	/**
	 *@param arg1077
	 **/
	protected final void hardCall(Datum arg1077){
		super.callRemote(1076, 1077, arg1077);
	}
	/**
	 *@param arg1102
	 **/
	protected final void tellActionCall(Datum arg1102){
		super.callRemote(1101, 1102, arg1102);
	}
	/**
	 *@param arg1149
	 **/
	protected final void tellScreenSettingCall(Datum arg1149){
		super.callRemote(1148, 1149, arg1149);
	}
}