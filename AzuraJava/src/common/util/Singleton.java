package common.util;
public class Singleton {
	private static class Holder{private static Singleton instance = new Singleton();}
	public static Singleton me(){return Holder.instance;}
	private Singleton(){}
}