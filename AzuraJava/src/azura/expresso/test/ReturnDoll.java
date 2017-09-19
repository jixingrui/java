package azura.expresso.test;

public class ReturnDoll {
	
	private int id;

	public ReturnDoll(int id){
		this.id=id;
	}

	public void fire() {
		System.out.println("fired by " + id);
	}

}
