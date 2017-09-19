package azura.expresso.field;

import azura.expresso.Bean;
import azura.expresso.bean.BeanString;

public class FieldString extends FieldA {

	public FieldString(int idx) {
		super(idx);
	}

	@Override
	public Bean newBean() {
		return new BeanString();
	}

}
