package azura.expresso.field;

import azura.expresso.Bean;

public abstract class FieldA {
	public int idxBean;

	public FieldA(int idxBean) {
		this.idxBean = idxBean;
	}

	public abstract Bean newBean();
}
