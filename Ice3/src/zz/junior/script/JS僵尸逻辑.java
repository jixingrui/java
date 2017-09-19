package zz.junior.script;

import org.apache.log4j.Logger;
import azura.junior.client.JuniorInputI;
import azura.junior.client.ScriptA;

public abstract class JS僵尸逻辑 extends ScriptA {
	public static final Logger log = Logger.getLogger(JS僵尸逻辑.class);

	public JS僵尸逻辑(JuniorInputI client) {
		super(12, client);
		client.newScene(this);
	}

	//========= role ===========
	public final int r僵尸_僵尸=1;

	//========= input ===========

	//========= ask ===========

	@Override
	protected final boolean ask(int concept){
		switch(concept){
		}
		throw new Error();
	}

	//========= output ===========
	/**
	* 每当僵尸感知到一个枪手，就生成策略《僵尸对枪手》，僵尸扮演《僵尸对枪手》.僵尸；被感知的枪手扮演《僵尸对枪手》.枪手
	*/
	public abstract void o生成策略僵尸对枪手();

	@Override
	protected final void output(int concept){
		switch(concept){
		 case 22:{
			o生成策略僵尸对枪手();
			break;
		}
		}
	}
}