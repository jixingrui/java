package zz.junior.script;

import org.apache.log4j.Logger;
import azura.junior.client.JuniorInputI;
import azura.junior.client.ScriptA;

public abstract class JS枪手对僵尸 extends ScriptA {
	public static final Logger log = Logger.getLogger(JS枪手对僵尸.class);

	public JS枪手对僵尸(JuniorInputI client) {
		super(224, client);
		client.newScene(this);
	}

	//========= role ===========
	public final int r枪手_枪手=1;
	public final int r僵尸_僵尸=2;

	//========= input ===========
	public void i发现僵尸在枪手感知外(){
		client.input(this,233);
	}
	public void i通知_是目标僵尸(){
		client.input(this,426);
	}
	public void i发现僵尸在视野外(){
		client.input(this,392);
	}
	public void i发现僵尸在视野内(){
		client.input(this,394);
	}
	public void i发现僵尸在枪手射程内(){
		client.input(this,470);
	}
	public void i发现僵尸在枪手射程外(){
		client.input(this,472);
	}
	public void i枪手打完一下(){
		client.input(this,507);
	}
	public void i发现僵尸在警戒范围外(){
		client.input(this,388);
	}
	public void i发现僵尸在警戒范围内(){
		client.input(this,396);
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
	* 做匿名监视，放在我里。如果枪手和僵尸的距离>枪手感知，就点亮“我.发现僵尸在枪手感知外”
	*/
	public abstract void o加感知外监视();
	/**
	* 先销毁我里面的所有监视，再销毁我
	*/
	public abstract void o销毁我();
	/**
	* 做匿名监视，放在我里。如果枪手和僵尸的距离>枪手视野，就点亮“我.发现僵尸在枪手视野外”
	*/
	public abstract void o加视野外监视();
	/**
	* 做匿名监视，放在我里。如果枪手和僵尸的距离<=枪手视野，就点亮“我.发现僵尸在枪手视野内”
	*/
	public abstract void o加视野内监视();
	/**
	* 做匿名监视，放在我里。如果枪手和僵尸的距离<=枪手警戒范围，就点亮“我.发现僵尸在枪手视警戒范围内”
	*/
	public abstract void o加警戒范围内监视();
	/**
	* 做匿名监视，放在我里。如果枪手和僵尸的距离>枪手警戒范围，就点亮“我.发现僵尸在枪手视警戒范围外”
	*/
	public abstract void o加警戒范围外监视();
	/**
	* 做监视，起名“射程内监视”，放在我里。当枪手和僵尸的距离<=枪手射程，点亮“我.发现僵尸在枪手射程内”
	*/
	public abstract void o加射程内监视();
	/**
	* 做监视，起名“射程外监视”，放在我里。当枪手和僵尸的距离>枪手射程，点亮“我.发现僵尸在枪手射程外”
	*/
	public abstract void o加射程外监视();
	/**
	* 销毁我里面的“射程内监视”射程外监视“”
	*/
	public abstract void o销毁射程监视();
	public abstract void o枪手持续追僵尸();
	/**
	* 打完就点亮“我.枪手打完一下”
	*/
	public abstract void o枪手打僵尸一下();
	public abstract void o播放枪击动画();

	@Override
	protected final void output(int concept){
		switch(concept){
		 case 231:{
			o加感知外监视();
			break;
		}
		 case 235:{
			o销毁我();
			break;
		}
		 case 382:{
			o加视野外监视();
			break;
		}
		 case 384:{
			o加视野内监视();
			break;
		}
		 case 386:{
			o加警戒范围内监视();
			break;
		}
		 case 390:{
			o加警戒范围外监视();
			break;
		}
		 case 464:{
			o加射程内监视();
			break;
		}
		 case 466:{
			o加射程外监视();
			break;
		}
		 case 468:{
			o销毁射程监视();
			break;
		}
		 case 498:{
			o枪手持续追僵尸();
			break;
		}
		 case 500:{
			o枪手打僵尸一下();
			break;
		}
		 case 504:{
			o播放枪击动画();
			break;
		}
		}
	}
}