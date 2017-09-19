package old.azura.avalon.ice.i;

import java.util.Collection;

import common.collections.RectB;
import old.azura.avalon.ice.Stander;

public interface RangeI {

	RectB getRange();

	int getZ();

	void capture(Collection<Stander> list);
}
