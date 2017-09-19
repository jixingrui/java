package zz.junior.script;

import org.apache.log4j.Logger;
import azura.junior.client.JuniorInputI;
import azura.junior.client.ScriptA;

public abstract class JS初始化说明 extends ScriptA {
	public static final Logger log = Logger.getLogger(JS初始化说明.class);

	public JS初始化说明(JuniorInputI client) {
		super(5, client);
		client.newScene(this);
	}

	//========= role ===========

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
	* 每次生成一个枪手，就捆绑职业枪手，一张枪手的图片。
	*<p>生成策略《枪手逻辑》，捆绑它，枪手扮演《枪手逻辑》.枪手
	*/
	public abstract void o生成容器枪手();
	/**
	* 每次生成一个容器僵尸，就捆绑职业僵尸，一张僵尸的图片，生成策略《僵尸逻辑》，捆绑它，僵尸扮演《僵尸逻辑》.僵尸
	*/
	public abstract void o生成容器僵尸();

	@Override
	protected final void output(int concept){
		switch(concept){
		 case 8:{
			o生成容器枪手();
			break;
		}
		 case 10:{
			o生成容器僵尸();
			break;
		}
		}
	}
}