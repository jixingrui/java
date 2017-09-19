package azura.expresso.field;

import azura.expresso.Bean;
import azura.expresso.NameSpace;
import azura.expresso.bean.BeanDatum;

public class FieldDatum extends FieldA {
	private int type;
	private NameSpace ns;

	public FieldDatum(int idx, int type, NameSpace ns) {
		super(idx);
		this.type = type;
		this.ns=ns;
	}

	@Override
	public Bean newBean() {
		return new BeanDatum(type,ns.newDatum(type));
	}

}
