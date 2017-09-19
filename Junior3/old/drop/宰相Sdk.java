package azura.junior.drop;

import azura.junior.client.Client;
import azura.junior.client.JuniorSdk;

public abstract class 宰相Sdk extends JuniorSdk {
	public static final long type = 1979803774;

	public 宰相Sdk(Client client) {
		super(client);
	}

	//========= input ===========
	public void i下雨了(){
		client.input(this,1979803835);
	}
	public void i干旱(){
		client.input(this,1979803925);
	}
	public void i存粮不足三月(){
		client.input(this,1979834238);
	}
	public void i存粮足够一年(){
		client.input(this,1979834357);
	}

	//========= output ===========
	public abstract void 报喜();
	public abstract void 请陛下振作();
	public abstract void 计算存粮();
	public abstract void 计算武力是否足够();

	@Override
	protected final void output(int msg){
		switch(msg){
		 case 1979804383:{
			报喜();
			break;
		}
		 case 1979804093:{
			请陛下振作();
			break;
		}
		 case 1979833769:{
			计算存粮();
			break;
		}
		 case 1979833793:{
			计算武力是否足够();
			break;
		}
		}
	}
}