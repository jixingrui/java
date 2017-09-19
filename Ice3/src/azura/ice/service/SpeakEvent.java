package azura.ice.service;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class SpeakEvent implements BytesI {

	public ActionE action;
	public int angle;

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeUTFZ(action.name());
		zb.writeZint(angle);
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		action = ActionE.valueOf(zb.readUTFZ());
		angle = zb.readZint();
	}

}
