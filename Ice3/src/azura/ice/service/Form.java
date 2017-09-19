package azura.ice.service;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Form implements BytesI {

	public String skin;
	public ActionE action;
	public int speed;

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeUTFZ(skin);
		zb.writeUTFZ(action.name());
		zb.writeZint(speed);
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		skin = zb.readUTFZ();
		action = ActionE.valueOf(zb.readUTFZ());
		speed = zb.readZint();
	}

}
