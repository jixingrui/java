package azura.expresso.field;

import azura.expresso.Bean;
import azura.expresso.bean.BeanBytes;

public class FieldBytes extends FieldA {

	public FieldBytes(int idx) {
		super(idx);
	}

	@Override
	public Bean newBean() {
		return new BeanBytes();
	}

}
