package azura.banshee.zui.model.v2016041415;

import azura.banshee.zui.model.v2016061713.MassActionE0617;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class MassAction0414 implements BytesI {

	public int type;
	public String targetPath="";
	public int internal_event_coder;
	public String message="";

	public boolean isEmpty() {
		if (internal_event_coder == 0 && targetPath.length() == 0)
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		MassActionE0617 e = MassActionE0617.values()[type];
		sb.append(e.name()).append(">[").append(targetPath).append("]");
		if (internal_event_coder == 1)
			sb.append("\tevent=[").append(message).append("]");
		else if (internal_event_coder == 2)
			sb.append("\tcoder: ").append(message);
		return sb.toString();
	}

	public void clear() {
		type = 0;
		targetPath = "";
		internal_event_coder = 0;
		message = "";
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		type = zb.readZint();
		targetPath = zb.readUTFZ();
		internal_event_coder = zb.readZint();
		message = zb.readUTFZ();
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(type);
		zb.writeUTFZ(targetPath);
		zb.writeZint(internal_event_coder);
		zb.writeUTFZ(message);
		return zb.toBytes();
	}

}
