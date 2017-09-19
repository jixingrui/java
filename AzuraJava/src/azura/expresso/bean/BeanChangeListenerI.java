package azura.expresso.bean;

import common.collections.buffer.i.ZintCodecI;

public interface BeanChangeListenerI {
	public void change(ZintCodecI newValue, ZintCodecI oldValue);
}
