package azura.junior.drop;

import azura.junior.client.Client;
import azura.junior.client.JuniorSdk;

public abstract class 将领Sdk extends JuniorSdk {
	public static final long type = 1979834462;

	public 将领Sdk(Client client) {
		super(client);
	}

	//========= input ===========
	public void i听说国王斩了个贪官(){
		client.input(this,1979834540);
	}
	public void i遇到了个贪污的机会(){
		client.input(this,1979834656);
	}

	//========= output ===========
	public abstract void 我要贪污一下();

	@Override
	protected final void output(int msg){
		switch(msg){
		 case 1979834809:{
			我要贪污一下();
			break;
		}
		}
	}
}