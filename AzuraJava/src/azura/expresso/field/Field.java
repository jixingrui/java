package azura.expresso.field;

import common.collections.buffer.i.ZintCodecI;

public abstract class Field {
	public int idx;
	
	public Field(int idx){
		this.idx=idx;
	}

	public abstract ZintCodecI newBean();
}
