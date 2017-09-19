package azura.banshee.zui.model.v2016061713;

import azura.banshee.zui.model.v2016041415.MassAction0414;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class MassAction0617 implements BytesI {

	public int type;
	public String targetPath = "";
	public MsgTypeE0617 msgType = MsgTypeE0617.slap;
	public int control = 0;
	public String message = "";

	public boolean isEmpty() {
		if (msgType == MsgTypeE0617.slap) {
			if (targetPath.length() == 0)
				return true;
			else
				return false;
		} else if (control == 0 && message.length() == 0)
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		MassActionE0617 e = MassActionE0617.values()[type];
		sb.append(e.name()).append(">[").append(targetPath).append("]");
		if (msgType == MsgTypeE0617.to_mass)
			sb.append("\tto_mass=[").append(message).append("]");
		else if (msgType == MsgTypeE0617.to_coder)
			sb.append("\tto_coder: ").append(message);
		else if (msgType == MsgTypeE0617.to_device)
			sb.append("\tto_device: (").append(control).append(message)
			.append(")").append(message);
		return sb.toString();
	}

	public void clear() {
		type = 0;
		targetPath = "";
		msgType = MsgTypeE0617.slap;
		control = 0;
		message = "";
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		type = zb.readZint();
		targetPath = zb.readUTFZ();
		msgType = MsgTypeE0617.values()[zb.readZint()];
		control = zb.readZint();
		message = zb.readUTFZ();
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(type);
		zb.writeUTFZ(targetPath);
		zb.writeZint(msgType.ordinal());
		zb.writeZint(control);
		zb.writeUTFZ(message);
		return zb.toBytes();
	}

	public void eat(MassAction0414 old) {
		this.type = old.type;
		this.targetPath = old.targetPath;
		this.msgType = MsgTypeE0617.values()[old.internal_event_coder];
		this.control = 0;
		this.message = old.message;
	}

}
