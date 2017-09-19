package zz.junior.identity;

import org.apache.log4j.Logger;
import azura.junior.client.JuniorInputI;
import azura.junior.client.ProA;

public abstract class JI枪手 extends ProA {
	public static final Logger log = Logger.getLogger(JI枪手.class);

	public JI枪手(JuniorInputI client) {
		super(1, client);
		client.newEgo(this);
	}

	//========= input ===========
	/**
	* 因为枪手不跑了，而被点亮
	*/
	public void i跑完停下(){
		client.input(this,297);
	}
	/**
	* 因为玩家操作而被点亮
	*/
	public void i玩家干预(){
		client.input(this,305);
	}

	//========= ask ===========

	@Override
	protected final boolean ask(int concept){
		switch(concept){
		}
		throw new Error();
	}

	//========= output ===========
	/**
	* 当玩家操作导致枪手开始跑步，就点亮“我.玩家干预”
	*<p>只要枪手不跑了，就点亮“我.跑完停下”。
	*/
	public abstract void o初始化();
	/**
	* 找最近的僵尸，找我对这个僵尸所在的《枪手对僵尸》，之后点亮《枪手对僵尸》.通知_是目标僵尸
	*/
	public abstract void o标记目标僵尸();
	/**
	* （目前此处代码是空的，原因是服务器和客户端有时间差，如果是真的发出站立不动，可能的坏现象有两种：1.枪手没跑到位就突然停下；2.枪手还在向前移动，但是播放了站立动画，看起来在飘。想要加上这段代码，还需逻辑考虑到时间差的问题，再进行完美设计）
	*/
	public abstract void o枪手站立不动();
	/**
	* 找到方向后逃跑一段
	*/
	public abstract void o逃跑一段();

	@Override
	protected final void output(int concept){
		switch(concept){
		 case 309:{
			o初始化();
			break;
		}
		 case 356:{
			o标记目标僵尸();
			break;
		}
		 case 360:{
			o枪手站立不动();
			break;
		}
		 case 374:{
			o逃跑一段();
			break;
		}
		}
	}
}