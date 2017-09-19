package zz.junior.script;

import org.apache.log4j.Logger;
import azura.junior.client.JuniorInputI;
import azura.junior.client.ScriptA;

public abstract class JS僵尸对枪手 extends ScriptA {
	public static final Logger log = Logger.getLogger(JS僵尸对枪手.class);

	public JS僵尸对枪手(JuniorInputI client) {
		super(15, client);
		client.newScene(this);
	}

	//========= role ===========
	public final int r僵尸_僵尸=1;
	public final int r枪手_枪手=2;

	//========= input ===========
	public void i发现枪手在僵尸感知外(){
		client.input(this,94);
	}
	public void i发现枪手在僵尸视野外(){
		client.input(this,99);
	}
	public void i发现枪手在僵尸视野内(){
		client.input(this,101);
	}
	public void i通知_是目标枪手(){
		client.input(this,145);
	}
	public void i咬完一下(){
		client.input(this,182);
	}
	public void i发现枪手在僵尸射程内(){
		client.input(this,201);
	}
	public void i发现枪手在僵尸射程外(){
		client.input(this,203);
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
	* 做匿名监视，放在我里。如果僵尸和枪手的距离>僵尸感知，就点亮“我.发现枪手在僵尸感知外”
	*/
	public abstract void o加感知外监视();
	/**
	* 做匿名监视，放在我里。如果僵尸和枪手的距离<=僵尸视野，就点亮“我.发现枪手在僵尸视野内”
	*/
	public abstract void o加视野内监视();
	/**
	* 做匿名监视，放在我里。如果僵尸和枪手的距离>僵尸视野，就点亮“我.发现枪手在僵尸视野外”
	*/
	public abstract void o加视野外监视();
	/**
	* 先销毁我里面的所有监视，再销毁我
	*/
	public abstract void o销毁我();
	/**
	* 做监视，起名“射程内监视”，放在我里。当僵尸和枪手的距离<=僵尸射程，点亮“我.发现枪手在僵尸射程内”
	*/
	public abstract void o加射程内监视();
	/**
	* 做监视，起名“射程外监视”，放在我里。当僵尸和枪手的距离>僵尸射程，点亮“我.发现枪手在僵尸射程外”
	*/
	public abstract void o加射程外监视();
	/**
	* 销毁我里面的“射程内监视”射程外监视“”
	*/
	public abstract void o销毁射程监视();
	/**
	* 播放僵尸追人动画，之后持续追枪手
	*/
	public abstract void o僵尸持续追枪手();
	/**
	* 咬完就点亮“我.咬完一下”
	*/
	public abstract void o咬枪手一下();
	public abstract void o播放咬人动画();

	@Override
	protected final void output(int concept){
		switch(concept){
		 case 46:{
			o加感知外监视();
			break;
		}
		 case 48:{
			o加视野内监视();
			break;
		}
		 case 92:{
			o加视野外监视();
			break;
		}
		 case 96:{
			o销毁我();
			break;
		}
		 case 151:{
			o加射程内监视();
			break;
		}
		 case 153:{
			o加射程外监视();
			break;
		}
		 case 155:{
			o销毁射程监视();
			break;
		}
		 case 178:{
			o僵尸持续追枪手();
			break;
		}
		 case 180:{
			o咬枪手一下();
			break;
		}
		 case 184:{
			o播放咬人动画();
			break;
		}
		}
	}
}