package azura.junior.drop;

import azura.junior.client.Client;
import azura.junior.client.JuniorSdk;

public abstract class 农田Sdk extends JuniorSdk {
	public static final long type = 1979835246;

	public 农田Sdk(Client client) {
		super(client);
	}

	//========= input ===========
	public void i干旱(){
		client.input(this,1979835290);
	}
	public void i收重税(){
		client.input(this,1979835347);
	}

	//========= output ===========
	public abstract void 起义();

	@Override
	protected final void output(int msg){
		switch(msg){
		 case 1979835344:{
			起义();
			break;
		}
		}
	}
}