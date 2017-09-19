package common.collections;

import common.collections.buffer.i.ZintCodecI;

public interface IdStoreI extends ZintCodecI {
	int getId();

	void setId(int value);
}
