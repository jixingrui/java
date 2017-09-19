package common.algorithm.crypto;

public class Rot {

//	public static void main(String[] args) {
//		byte[] data = "wow"
//				.getBytes();
//		process(data, false);
//		process(data, true);
//		System.out.println(new String(data));
//	}
	
	public static void encrypt(byte[] data){
		process(data,true);
	}

	public static void decrypt(byte[] data){
		process(data,false);
	}

	private static void process(byte[] data, boolean reverse) {
		int i = 0;
		int sign = (reverse) ? -1 : 1;
		int length=Math.min(1024, data.length);
		while (i < length) {
			data[i] += i * sign;
			i++;
		}
	}
}
