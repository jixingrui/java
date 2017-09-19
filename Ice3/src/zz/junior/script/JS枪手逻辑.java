package zz.junior.script;

import org.apache.log4j.Logger;
import azura.junior.client.JuniorInputI;
import azura.junior.client.ScriptA;

public abstract class JS枪手逻辑 extends ScriptA {
	public static final Logger log = Logger.getLogger(JS枪手逻辑.class);

	public JS枪手逻辑(JuniorInputI client) {
		super(221, client);
		client.newScene(this);
	}

	//========= role ===========
	public final int r枪手_枪手=1;

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
	* 每当枪手感知到一个僵尸，就生成策略《枪手对僵尸》，枪手扮演《枪手对僵尸》.枪手；被感知的僵尸扮演《枪手对僵尸》.僵尸
	*/
	public abstract void o生成策略枪手对僵尸();

	@Override
	protected final void output(int concept){
		switch(concept){
		 case 229:{
			o生成策略枪手对僵尸();
			break;
		}
		}
	}
}