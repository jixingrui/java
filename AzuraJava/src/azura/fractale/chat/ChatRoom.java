package azura.fractale.chat;

import java.nio.ByteBuffer;

public class ChatRoom {
	public byte capacity = 2;
	byte lifeSpanMin = 3;
	int currentTimeSec;
	int rand;

	public static String newKey() {
		ChatRoom cr = new ChatRoom();
		byte[] data = cr.toBytes();
		return UrlCodec.encode(data);
	}
	
//	public static void main(String[] args){
//		System.out.println(newKey());
//	}

	public boolean timeValid() {
		return Math.abs(currentTimeSec - System.currentTimeMillis() / 1000) < lifeSpanMin * 60;
	}

	public byte[] toBytes() {
		rand = (int) (Math.random() * Integer.MAX_VALUE);
		currentTimeSec = (int) (System.currentTimeMillis() / 1000);

		ByteBuffer bb = ByteBuffer.allocate(10);
		bb.put(capacity);
		bb.put(lifeSpanMin);
		bb.putInt(currentTimeSec);
		bb.putInt(rand);
		bb.rewind();

		byte[] b = new byte[bb.remaining()];
		bb.get(b, 0, b.length);

		return b;
	}

	public void fromBytes(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data);
		capacity = bb.get();
		lifeSpanMin = bb.get();
		currentTimeSec = bb.getInt();
		rand = bb.getInt();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("capacity=" + capacity);
		sb.append(" lifeSpanMin=" + lifeSpanMin);
		sb.append(" currentTimeSec=" + currentTimeSec);
		sb.append(" rand=" + rand);
		return sb.toString();
	}
}
