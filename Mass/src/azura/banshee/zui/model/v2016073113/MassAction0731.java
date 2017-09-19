package azura.banshee.zui.model.v2016073113;

import azura.banshee.zui.model.v2016061713.MassAction0617;
import azura.banshee.zui.model.v2016061713.MassActionE0617;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class MassAction0731 implements BytesI {

	public ByActionE0731 byType;
	public ActionToE0731 toType;
	public String targetPath;
	public int intMsg;
	public String stringMsg;

	public MassAction0731() {
		clear();
	}

	public void clear() {
		byType = null;
		toType = null;
		targetPath = "";
		intMsg = 0;
		stringMsg = "";
	}

	public boolean isEmpty() {
		if (toType == ActionToE0731.do_activate
				|| toType == ActionToE0731.do_spank
				|| toType == ActionToE0731.do_clear) {
			return targetPath.length() == 0;
		} else if (toType == ActionToE0731.to_coder) {
			return (targetPath.length() == 0 && stringMsg.length() == 0);
		} else if (toType == ActionToE0731.to_mass) {
			return stringMsg.length() == 0;
		} else if (toType == ActionToE0731.to_device) {
			return intMsg == 0;
		}

		return false;
	}

	public boolean isInternal() {
		return toType == ActionToE0731.do_activate
				|| toType == ActionToE0731.do_spank
				|| toType == ActionToE0731.do_clear;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(byType.name()).append(">").append(toType.name()).append(">");
		if (toType == ActionToE0731.to_coder)
			sb.append("[").append(targetPath).append("]").append("{")
					.append(stringMsg).append("}");
		else if (toType == ActionToE0731.to_mass)
			sb.append("{").append(stringMsg).append("}");
		else if (toType == ActionToE0731.to_device)
			sb.append("(").append(intMsg).append("){").append(stringMsg)
					.append("}");
		else
			sb.append("[").append(targetPath).append("]");
		return sb.toString();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		byType = ByActionE0731.values()[zb.readZint()];
		int toTypeV = zb.readZint();
		if (toTypeV == -1)
			toType = null;
		else
			toType = ActionToE0731.values()[toTypeV];
		targetPath = zb.readUTFZ();
		intMsg = zb.readZint();
		stringMsg = zb.readUTFZ();
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(byType.ordinal());
		if (toType == null)
			zb.writeZint(-1);
		else
			zb.writeZint(toType.ordinal());
		zb.writeUTFZ(targetPath);
		zb.writeZint(intMsg);
		zb.writeUTFZ(stringMsg);
		return zb.toBytes();
	}

	public void eat(MassAction0617 old) {
		this.targetPath = old.targetPath;
		this.intMsg = old.control;
		this.stringMsg = old.message;

		MassActionE0617 oldType = MassActionE0617.values()[old.type];
		switch (oldType) {
		case hover:
			this.byType = ByActionE0731.by_hover;
			break;
		case out:
			this.byType = ByActionE0731.by_out;
			break;
		case single_click:
			this.byType = ByActionE0731.by_single;
			break;
		case double_click:
			this.byType = ByActionE0731.by_double;
			break;
		case spank:
			this.byType = ByActionE0731.by_spank;
			break;
		case activate:
			this.byType = ByActionE0731.by_activate;
			break;
		}

		switch (old.msgType) {
		case slap:
			this.toType = ActionToE0731.do_activate;
			break;
		case to_coder:
			this.toType = ActionToE0731.to_coder;
			break;
		case to_device:
			this.toType = ActionToE0731.to_device;
			break;
		case to_mass:
			this.toType = ActionToE0731.to_mass;
			break;
		}
	}
}
