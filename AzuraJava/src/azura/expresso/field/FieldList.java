package azura.expresso.field;

import azura.expresso.Bean;
import azura.expresso.bean.BeanList;

public class FieldList extends FieldA {

	FieldA type;

	public FieldList(int idx, FieldA type) {
		super(idx);
		this.type = type;
	}

	@Override
	public Bean newBean() {
		return new BeanList(type);
	}

}
