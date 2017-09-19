package azura.expresso.field;

import azura.expresso.Bean;
import azura.expresso.bean.BeanDouble;

public class FieldDouble extends FieldA {

	public FieldDouble(int idx) {
		super(idx);
	}

	@Override
	public Bean newBean() {
		return new BeanDouble();
	}

}
