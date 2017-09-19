package azura.helios.hard10.ie;

import java.util.List;

import azura.helios.hard10.HardItem;

public interface HardClientI {

	void rename(String name);

	void append(boolean up_down, List<HardItem> list);

	void terminal(boolean up_down);

	void update(boolean up_down, int idx, HardItem item);

	void delete(boolean up_down, int idx);

	void clear(boolean up_down);

	void unselect();
}
