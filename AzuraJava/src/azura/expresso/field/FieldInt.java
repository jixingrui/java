package azura.expresso.field;

import azura.expresso.Bean;
import azura.expresso.bean.BeanInt;

public class FieldInt extends FieldA {

	public FieldInt(int idx) {
		super(idx);
	}

	@Override
	public Bean newBean() {
		return new BeanInt();
	}

}
