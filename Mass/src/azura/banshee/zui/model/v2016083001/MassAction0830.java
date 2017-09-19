package azura.banshee.zui.model.v2016083001;

import azura.banshee.zui.model.v2016073113.MassAction0731;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class MassAction0830 implements BytesI {

	public ByActionE0830 byType;
	public ActionToE0830 toType;
	public String targetPath;
	public int intMsg;
	public String stringMsg;

	public MassAction0830() {
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
		if (toType == ActionToE0830.do_slap
				|| toType == ActionToE0830.do_spank
				|| toType == ActionToE0830.do_reset) {
			return targetPath.length() == 0;
		} else if (toType == ActionToE0830.to_coder) {
			return (targetPath.length() == 0 && stringMsg.length() == 0);
		} else if (toType == ActionToE0830.to_mass) {
			return stringMsg.length() == 0;
		} else if (toType == ActionToE0830.to_device) {
			return intMsg == 0;
		}

		return false;
	}

	public boolean isInternal() {
		return toType == ActionToE0830.do_slap
				|| toType == ActionToE0830.do_spank
				|| toType == ActionToE0830.do_reset;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(byType.name()).append(">").append(toType.name()).append(">");
		if (toType == ActionToE0830.to_coder)
			sb.append("[").append(targetPath).append("]").append("{")
					.append(stringMsg).append("}");
		else if (toType == ActionToE0830.to_mass)
			sb.append("{").append(stringMsg).append("}");
		else if (toType == ActionToE0830.to_device)
			sb.append("(").append(intMsg).append("){").append(stringMsg)
					.append("}");
		else
			sb.append("[").append(targetPath).append("]");
		return sb.toString();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		byType = ByActionE0830.values()[zb.readZint()];
		int toTypeV = zb.readZint();
		if (toTypeV == -1)
			toType = null;
		else
			toType = ActionToE0830.values()[toTypeV];
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

	public void eat(MassAction0731 old) {
		this.byType = ByActionE0830.values()[old.byType.ordinal()];
		this.toType = ActionToE0830.values()[old.toType.ordinal()];
		this.targetPath = old.targetPath;
		this.intMsg = old.intMsg;
		this.stringMsg = old.stringMsg;
	}
}
