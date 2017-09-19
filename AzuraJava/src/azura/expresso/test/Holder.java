package azura.expresso.test;

public class Holder {

	private Runnable runnable;

	public void listen(Runnable runnable) {
		this.runnable=runnable;
	}

	public void fire() {
		runnable.run();
	}

}
