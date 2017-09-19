package azura.expresso.field;

import azura.expresso.Bean;
import azura.expresso.bean.BeanBoolean;

public class FieldBoolean extends FieldA {

	public FieldBoolean(int idx) {
		super(idx);
	}

	@Override
	public Bean newBean() {
		return new BeanBoolean();
	}

}
