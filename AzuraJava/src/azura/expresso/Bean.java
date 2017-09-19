package azura.expresso;

import java.util.concurrent.atomic.AtomicBoolean;

import azura.expresso.bean.BeanChangeListenerI;
import azura.expresso.bean.BeanListI;
import common.collections.buffer.i.ZintCodecI;

public abstract class Bean implements ZintCodecI {
	protected AtomicBoolean changed = new AtomicBoolean();
	private BeanChangeListenerI listener;

	public void addEventListener(BeanChangeListenerI listener) {
		this.listener = listener;
	}

	public void change(Bean pray) {
		if (listener != null) {
			listener.change(pray, this);
		}
		eat(pray);
	}

	public abstract void eat(Bean pray);

	public abstract int asInt();

	public abstract String asString();

	public abstract boolean asBoolean();

	public abstract byte[] asBytes();

	public abstract double asDouble();

	public abstract Datum asDatum();

	public abstract BeanListI asList();

	public abstract void setInt(int value);

	public abstract void setString(String value);

	public abstract void setBoolean(boolean value);

	public abstract void setBytes(byte[] value);

	public abstract void setDouble(double value);

	public abstract void setDatum(Datum value);

	// public abstract void setList(List<Bean> value);

}
