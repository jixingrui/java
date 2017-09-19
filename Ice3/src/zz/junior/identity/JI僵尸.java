package zz.junior.identity;

import org.apache.log4j.Logger;
import azura.junior.client.JuniorInputI;
import azura.junior.client.ProA;

public abstract class JI僵尸 extends ProA {
	public static final Logger log = Logger.getLogger(JI僵尸.class);

	public JI僵尸(JuniorInputI client) {
		super(3, client);
		client.newEgo(this);
	}

	//========= input ===========

	//========= ask ===========

	@Override
	protected final boolean ask(int concept){
		switch(concept){
		}
		throw new Error();
	}

	//========= output ===========
	public abstract void o僵尸站立不动();
	/**
	* 找最近的枪手，找我对这个枪手所在的《僵尸对枪手》，之后点亮《僵尸对枪手》.通知_是目标枪手
	*/
	public abstract void o标记目标枪手();

	@Override
	protected final void output(int concept){
		switch(concept){
		 case 41:{
			o僵尸站立不动();
			break;
		}
		 case 59:{
			o标记目标枪手();
			break;
		}
		}
	}
}